package com.mercadopago.android.px.internal.features.payment_result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.addons.FlowBehaviour;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.actions.ChangePaymentMethodAction;
import com.mercadopago.android.px.internal.actions.CopyAction;
import com.mercadopago.android.px.internal.actions.LinkAction;
import com.mercadopago.android.px.internal.actions.NextAction;
import com.mercadopago.android.px.internal.actions.RecoverPaymentAction;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton;
import com.mercadopago.android.px.internal.features.one_tap.RenderMode;
import com.mercadopago.android.px.internal.features.pay_button.PaymentState;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModelMapper;
import com.mercadopago.android.px.internal.features.payment_result.mappers.PaymentResultViewModelMapper;
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultButton;
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultFooter;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.internal.features.security_code.RenderModeMapper;
import com.mercadopago.android.px.internal.features.security_code.model.SecurityCodeParams;
import com.mercadopago.android.px.internal.mappers.FlowBehaviourResultMapper;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.Action;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.internal.CongratsResponse;
import com.mercadopago.android.px.tracking.internal.BankInfoHelper;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import com.mercadopago.android.px.tracking.internal.events.ChangePaymentMethodEvent;
import com.mercadopago.android.px.tracking.internal.events.CongratsSuccessDeepLink;
import com.mercadopago.android.px.tracking.internal.events.ContinueEvent;
import com.mercadopago.android.px.tracking.internal.events.CrossSellingEvent;
import com.mercadopago.android.px.tracking.internal.events.DeepLinkType;
import com.mercadopago.android.px.tracking.internal.events.DiscountItemEvent;
import com.mercadopago.android.px.tracking.internal.events.DownloadAppEvent;
import com.mercadopago.android.px.tracking.internal.events.ScoreEvent;
import com.mercadopago.android.px.tracking.internal.events.SeeAllDiscountsEvent;
import com.mercadopago.android.px.tracking.internal.events.ViewReceiptEvent;
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack;

/* default */ class PaymentResultPresenter extends BasePresenter<PaymentResult.View>
    implements ActionDispatcher, PaymentResult.Presenter, PaymentResult.Listener {

    private final PaymentModel paymentModel;
    @NonNull private final PaymentResultViewModelMapper paymentResultViewModelMapper;
    @NonNull /* default */ final PaymentCongratsModelMapper paymentCongratsMapper;
    private final FlowBehaviour flowBehaviour;
    @Nullable /* default */ CongratsAutoReturn autoReturnTimer;
    @NonNull private final RenderModeMapper renderModeMapper;

    /* default */ PaymentResultPresenter(@NonNull final PaymentSettingRepository paymentSettings,
        @NonNull final PaymentModel paymentModel,
        @NonNull final FlowBehaviour flowBehaviour, final boolean isMP,
        @NonNull final PaymentCongratsModelMapper paymentCongratsMapper,
        @NonNull final PaymentResultViewModelMapper paymentResultViewModelMapper,
        @NonNull final RenderModeMapper renderModeMapper,
        @NonNull final BankInfoHelper bankInfoHelper,
        @NonNull final MPTracker tracker) {
        super(tracker);
        this.paymentModel = paymentModel;
        this.flowBehaviour = flowBehaviour;
        this.paymentCongratsMapper = paymentCongratsMapper;
        this.renderModeMapper = renderModeMapper;

        final PaymentResultScreenConfiguration screenConfiguration =
            paymentSettings.getAdvancedConfiguration().getPaymentResultScreenConfiguration();
        this.paymentResultViewModelMapper = paymentResultViewModelMapper;
        setViewTrack(new ResultViewTrack(paymentModel, screenConfiguration, paymentSettings, isMP, bankInfoHelper));
    }

    @Override
    public void attachView(@NonNull final PaymentResult.View view) {
        super.attachView(view);
        configureView();
    }

    @Override
    public void onFreshStart() {
        trackView();
        final IPaymentDescriptor payment = paymentModel.getPayment();
        if (payment != null) {
            flowBehaviour.trackConversion(new FlowBehaviourResultMapper().map(payment));
        } else {
            flowBehaviour.trackConversion();
        }
    }

    @Override
    public void onStart() {
        if (autoReturnTimer != null) {
            autoReturnTimer.start();
        }
    }

    @Override
    public void onStop() {
        if (autoReturnTimer != null) {
            autoReturnTimer.cancel();
        }
    }

    @Override
    public void onAbort() {
        trackAbort();
        finishWithResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
    }

    @Override
    public void onGetViewTrackPath(@NonNull final ConfirmButton.ViewTrackPathCallback callback) {
        callback.call(getViewTrack().getTrack().getPath());
    }

    @Override
    public void onCvvRequested(@NonNull final PaymentState paymentState) {
        final SecurityCodeParams params = new SecurityCodeParams(
            paymentState.getPaymentConfiguration(),
            renderModeMapper.map(RenderMode.DYNAMIC),
            paymentState.getCard(),
            paymentState.getPaymentRecovery(),
            paymentState.getReason()
        );

        getView().showSecurityCodeScreen(params);
    }

    private void configureView() {
        if (isViewAttached()) {
            final PaymentResultViewModel viewModel = paymentResultViewModelMapper.map(paymentModel);
            getView().configureViews(viewModel, paymentModel, this, new PaymentResultFooter.Listener() {
                @Override
                public void onClick(@NonNull final PaymentResultButton.Action action) {
                    //Only known action at this moment.
                    if (action == PaymentResultButton.Action.CONTINUE) {
                        dispatch(new NextAction());
                    }
                }

                @Override
                public void onClick(@NonNull final String target) {
                    getView().launchDeepLink(target);
                }
            });
            getView().setStatusBarColor(viewModel.getHeaderModel().getBackgroundColor());
            final CongratsAutoReturn.Model autoReturnModel = viewModel.getAutoReturnModel();
            if (autoReturnModel != null) {
                autoReturnTimer = new CongratsAutoReturn(autoReturnModel, new CongratsAutoReturn.Listener() {
                    @Override
                    public void onFinish() {
                        autoReturnTimer = null;
                        onAbort();
                    }

                    @Override
                    public void updateView(@NonNull final String label) {
                        getView().updateAutoReturnLabel(label);
                    }
                });
            }
        }
    }

    @Override
    public void dispatch(@NonNull final Action action) {
        if (!isViewAttached()) {
            return;
        }

        if (action instanceof NextAction) {
            track(new ContinueEvent(getViewTrack()));
            finishWithResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        } else if (action instanceof ChangePaymentMethodAction) {
            track(new ChangePaymentMethodEvent(false));
            getView().changePaymentMethod();
        } else if (action instanceof RecoverPaymentAction) {
            getView().recoverPayment();
        }
    }

    @Override
    public void onLinkAction(@NonNull final LinkAction action) {
        if (isViewAttached()) {
            getView().openLink(action.url);
        }
    }

    @Override
    public void onCopyAction(@NonNull final CopyAction action) {
        if (isViewAttached()) {
            getView().copyToClipboard(action.content);
        }
    }

    @Override
    public void OnClickDownloadAppButton(@NonNull final String deepLink) {
        track(new DownloadAppEvent(getViewTrack()));
        getView().launchDeepLink(deepLink);
    }

    @Override
    public void OnClickCrossSellingButton(@NonNull final String deepLink) {
        track(new CrossSellingEvent(getViewTrack()));
        getView().processCrossSellingBusinessAction(deepLink);
    }

    @Override
    public void onClickLoyaltyButton(@NonNull final String deepLink) {
        track(new ScoreEvent(getViewTrack()));
        getView().launchDeepLink(deepLink);
    }

    @Override
    public void onClickShowAllDiscounts(@NonNull final String deepLink) {
        track(new SeeAllDiscountsEvent(getViewTrack()));
        getView().launchDeepLink(deepLink);
    }

    @Override
    public void onClickViewReceipt(@NonNull final String deeLink) {
        track(new ViewReceiptEvent(getViewTrack()));
        getView().launchDeepLink(deeLink);
    }

    @Override
    public void onClickTouchPoint(@Nullable final String deepLink) {
        track(new DiscountItemEvent(getViewTrack(), 0, TextUtil.EMPTY));
        if (deepLink != null) {
            getView().launchDeepLink(deepLink);
        }
    }

    @Override
    public void onClickDiscountItem(final int index, @Nullable final String deepLink, @Nullable final String trackId) {
        track(new DiscountItemEvent(getViewTrack(), index, trackId));
        if (deepLink != null) {
            getView().launchDeepLink(deepLink);
        }
    }

    @Override
    public void onClickMoneySplit() {
        final CongratsResponse.MoneySplit moneySplit = paymentModel.getCongratsResponse().getMoneySplit();
        final String deepLink;
        if (moneySplit != null && (deepLink = moneySplit.getAction().getTarget()) != null) {
            track(new CongratsSuccessDeepLink(DeepLinkType.MONEY_SPLIT_TYPE, deepLink));
            getView().launchDeepLink(deepLink);
        }
    }

    private void finishWithResult(final int resultCode) {
        final CongratsResponse congratsResponse = paymentModel.getCongratsResponse();
        getView().finishWithResult(resultCode, congratsResponse.getBackUrl(), congratsResponse.getRedirectUrl());
    }
}
