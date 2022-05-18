package com.mercadopago.android.px.internal.features.payment_result;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Space;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.mercadolibre.android.andesui.snackbar.AndesSnackbar;
import com.mercadolibre.android.andesui.snackbar.duration.AndesSnackbarDuration;
import com.mercadolibre.android.andesui.snackbar.type.AndesSnackbarType;
import com.mercadolibre.android.mlbusinesscomponents.components.touchpoint.tracking.print.MLBusinessTouchpointListener;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.addons.BehaviourProvider;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.di.MapperProvider;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.extensions.BaseExtensionsKt;
import com.mercadopago.android.px.internal.features.Constants;
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton;
import com.mercadopago.android.px.internal.features.pay_button.PayButton;
import com.mercadopago.android.px.internal.features.pay_button.PayButtonFragment;
import com.mercadopago.android.px.internal.features.pay_button.PaymentState;
import com.mercadopago.android.px.internal.features.payment_result.components.PaymentResultLegacyRenderer;
import com.mercadopago.android.px.internal.features.payment_result.instruction.Instruction;
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultFooter;
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesFragment;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.internal.features.security_code.SecurityCodeActivity;
import com.mercadopago.android.px.internal.features.security_code.model.SecurityCodeParams;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.Logger;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;
import com.mercadopago.android.px.internal.viewmodel.ChangePaymentMethodPostPaymentAction;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction;
import com.mercadopago.android.px.internal.viewmodel.RecoverPaymentPostPaymentAction;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import kotlin.Unit;

import static com.mercadopago.android.px.internal.features.Constants.REQ_CODE_SECURITY_CODE;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_ACTION;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_CUSTOM_EXIT;
import static com.mercadopago.android.px.internal.util.MercadoPagoUtil.getSafeIntent;
import static com.mercadopago.android.px.internal.util.MercadoPagoUtil.isMP;

public class PaymentResultActivity extends PXActivity<PaymentResultPresenter> implements
    PaymentResult.View, PayButton.Handler, RemediesFragment.Listener {

    private static final String TAG = PaymentResultActivity.class.getSimpleName();
    private static final String TAG_PAY_BUTTON = "TAG_PAY_BUTTON";
    public static final String EXTRA_PAYMENT_MODEL = "extra_payment_model";
    public static final String EXTRA_RESULT_CODE = "extra_result_code";
    private PayButtonFragment payButtonFragment;
    private RemediesFragment remediesFragment;
    private PaymentResultFooter footer;
    private PaymentResultHeader header;
    private PaymentResultBody body;
    private Instruction instructions;
    private ScrollView scrollView;
    private Space space;

    public static void start(
        @NonNull final Fragment fragment,
        final int requestCode,
        @NonNull final PaymentModel model
    ) {
        final Activity activity = fragment.getActivity();
        if (activity instanceof PXActivity) {
            ((PXActivity) activity).overrideTransitionIn();
        }
        final Intent intent = new Intent(fragment.getContext(), PaymentResultActivity.class);
        intent.putExtra(EXTRA_PAYMENT_MODEL, model);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void start(
        @NonNull final Activity activity,
        final int requestCode,
        @NonNull final PaymentModel model
    ) {
        if (activity instanceof PXActivity) {
            ((PXActivity) activity).overrideTransitionIn();
        }
        final Intent intent = new Intent(activity, PaymentResultActivity.class);
        intent.putExtra(EXTRA_PAYMENT_MODEL, model);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        setContentView(R.layout.px_activity_payment_result);

        footer = findViewById(R.id.footer);
        instructions = findViewById(R.id.instructions);
        header = findViewById(R.id.header);
        body = findViewById(R.id.body);
        scrollView = findViewById(R.id.scroll_view);
        space = findViewById(R.id.space);

        presenter = createPresenter();
        presenter.attachView(this);
        if (savedInstanceState == null) {
            presenter.onFreshStart();
        }
        new MLBusinessTouchpointListener().setOnTouchListener(findViewById(R.id.scroll_view));
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @NonNull
    private PaymentResultPresenter createPresenter() {
        final PaymentModel paymentModel = getIntent().getParcelableExtra(EXTRA_PAYMENT_MODEL);
        final Session session = Session.getInstance();
        final MapperProvider mapperProvider = MapperProvider.INSTANCE;
        return new PaymentResultPresenter(session.getConfigurationModule().getPaymentSettings(),
            paymentModel, BehaviourProvider.getFlowBehaviour(), isMP(this),
            mapperProvider.getPaymentCongratsMapper(), mapperProvider.getPaymentResultViewModelMapper(),
            mapperProvider.getRenderModeMapper(this),
            session.getHelperModule().getBankInfoHelper(),
            session.getTracker());
    }

    @Override
    public void configureViews(@NonNull final PaymentResultViewModel model, @NonNull final PaymentModel paymentModel,
        @NonNull final PaymentResult.Listener listener, @NonNull final PaymentResultFooter.Listener footerListener) {
        header.setModel(model.getHeaderModel());
        if (model.getRemediesModel().hasRemedies()) {
            body.setVisibility(View.GONE);
            space.setVisibility(View.GONE);
            loadRemedies(paymentModel, model);
        } else {
            space.setVisibility(View.VISIBLE);
            final Instruction.Model instructionModel = model.getInstructionModel();
            if (instructionModel != null) {
                instructions.setVisibility(View.VISIBLE);
                instructions.init(instructionModel, listener);
            } else {
                body.init(model.getBodyModel(), listener);
            }
            final PaymentResultFooter.Model footerModel = model.getFooterModel();
            if (footerModel != null) {
                footer.init(footerModel, footerListener);
            }
            //TODO migrate
            final boolean shouldDrawLegacyFooter = footerModel == null;
            if (shouldDrawLegacyFooter) {
                footer.setVisibility(View.GONE);
            }

            PaymentResultLegacyRenderer.render(
                findViewById(R.id.container),
                listener,
                model.getLegacyViewModel(),
                shouldDrawLegacyFooter,
                instructionModel == null,
                Session.getInstance().getPaymentResultViewModelFactory()
            );
        }
    }

    @Override
    public void updateAutoReturnLabel(@NonNull final String label) {
        footer.showAutoReturn();
        footer.updateAutoReturnLabel(label);
    }

    private void loadRemedies(@NonNull final PaymentModel paymentModel, @NonNull final PaymentResultViewModel model) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        remediesFragment = (RemediesFragment) fragmentManager.findFragmentByTag(RemediesFragment.TAG);
        payButtonFragment = (PayButtonFragment) fragmentManager.findFragmentByTag(TAG_PAY_BUTTON);

        if (remediesFragment == null) {
            remediesFragment = RemediesFragment.newInstance(paymentModel, model.getRemediesModel());
            transaction
                .replace(R.id.remedies, remediesFragment, RemediesFragment.TAG);
        }
        if (payButtonFragment == null) {
            payButtonFragment = new PayButtonFragment();
            transaction
                .replace(R.id.confirm_button_container, payButtonFragment, TAG_PAY_BUTTON);
        }

        transaction.commitAllowingStateLoss();

        startKeyboardListener();
        final PaymentResultFooter.Model footerModel = model.getFooterModel();
        if (footerModel != null) {
            footer.init(footerModel, remediesFragment);
        }
        findViewById(R.id.remedies).setVisibility(View.VISIBLE);
    }

    @Override
    public void showApiExceptionError(@NonNull final ApiException exception, @NonNull final String requestOrigin) {
        ErrorUtil.showApiExceptionError(this, exception, requestOrigin);
    }

    @Override
    public void showInstructionsError() {
        ErrorUtil.startErrorActivity(this,
            new MercadoPagoError(getString(R.string.px_standard_error_message), false));
    }

    @Override
    public void onBackPressed() {
        if (payButtonFragment != null && payButtonFragment.isExploding()) {
            return;
        }
        presenter.onAbort();
    }

    @Override
    public void setStatusBarColor(@ColorRes final int color) {
        ViewUtils.setStatusBarColor(ContextCompat.getColor(this, color), getWindow());
    }

    @Override
    public void openLink(@NonNull final String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (final ActivityNotFoundException e) {
            Logger.debug(TAG, e);
        }
    }

    @Override
    public void finishWithResult(final int resultCode, @Nullable final String backUrl,
        @Nullable final String redirectUrl) {
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_CODE, resultCode);
        intent.putExtra(Constants.EXTRA_BACK_URL, backUrl);
        intent.putExtra(Constants.EXTRA_REDIRECT_URL, redirectUrl);
        setResult(RESULT_CUSTOM_EXIT, intent);
        finish();
    }

    @Override
    public void changePaymentMethod() {
        ViewUtils.hideKeyboard(this);
        final Intent returnIntent = new Intent();
        new ChangePaymentMethodPostPaymentAction().addToIntent(returnIntent);
        setResult(RESULT_ACTION, returnIntent);
        finish();
    }

    @Override
    public void onUserValidation() {
        changePaymentMethod();
    }

    @Override
    public void recoverPayment() {
        final Intent returnIntent = new Intent();
        new RecoverPaymentPostPaymentAction().addToIntent(returnIntent);
        setResult(RESULT_ACTION, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SECURITY_CODE && resultCode != Activity.RESULT_CANCELED) {
            setResult(resultCode, data);
            finish();
        }
    }

    @SuppressLint("Range")
    @Override
    public void copyToClipboard(@NonNull final String content) {
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText("", content);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            final View container = findViewById(R.id.container);
            new AndesSnackbar(container.getContext(), container, AndesSnackbarType.SUCCESS,
                getString(R.string.px_copied_to_clipboard_ack),
                AndesSnackbarDuration.SHORT).show();
        }
    }

    @Override
    public void launchDeepLink(@NonNull final String deepLink) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)));
        } catch (final ActivityNotFoundException e) {
            Logger.debug(TAG, e);
        }
    }

    @Override
    public void processCrossSellingBusinessAction(@NonNull final String deepLink) {
        try {
            startActivity(getSafeIntent(this, Uri.parse(deepLink)));
        } catch (final ActivityNotFoundException e) {
            Logger.debug(TAG, e);
        }
    }

    @Override
    public void getViewTrackPath(@NonNull final ConfirmButton.ViewTrackPathCallback callback) {
        presenter.onGetViewTrackPath(callback);
    }

    @Override
    public void onPreProcess(@NonNull final ConfirmButton.OnReadyForProcessCallback callback) {
        ViewUtils.hideKeyboard(this);
        remediesFragment.onPrePayment(callback);
    }

    @Override
    public void onEnqueueProcess(@NonNull final ConfirmButton.OnEnqueueResolvedCallback callback) {
        remediesFragment.onPayButtonPressed(callback);
    }

    @Override
    public void onCvvRequested(@NonNull final PaymentState paymentState) {
        presenter.onCvvRequested(paymentState);
    }

    @Override
    public void showSecurityCodeScreen(@NonNull final SecurityCodeParams params) {
        SecurityCodeActivity.start(this, params, REQ_CODE_SECURITY_CODE);
    }

    @Override
    public void enablePayButton() {
        payButtonFragment.enable();
    }

    @Override
    public void disablePayButton() {
        payButtonFragment.disable();
    }

    @Override
    public void onPostPaymentAction(@NonNull final PostPaymentAction postPaymentAction) {
        setResult(RESULT_ACTION, postPaymentAction.addToIntent(new Intent()));
        finish();
    }

    @Override
    public void onPostCongrats(final int resultCode, @Nullable final Intent data) {
        setResult(resultCode, data);
        finish();
    }

    private void startKeyboardListener() {
        BaseExtensionsKt.addKeyBoardListener(this, () -> {
            footer.hideSecondaryButton();
            scrollView.fullScroll(View.FOCUS_DOWN);
            return Unit.INSTANCE;
        }, () -> {
            footer.showSecondaryButton();
            return Unit.INSTANCE;
        });
    }

    @Override
    public void payFromModal() {
        payButtonFragment.stimulate();
    }
}
