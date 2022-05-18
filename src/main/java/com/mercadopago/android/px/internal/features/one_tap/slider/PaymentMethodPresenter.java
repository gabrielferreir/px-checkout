package com.mercadopago.android.px.internal.features.one_tap.slider;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.PayerCostSelectionRepository;
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import com.mercadopago.android.px.tracking.internal.events.ComboSwitchEvent;
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker;
import org.jetbrains.annotations.Nullable;

class PaymentMethodPresenter extends BasePresenter<PaymentMethod.View> implements PaymentMethod.Action {
    private final PayerCostSelectionRepository payerCostSelectionRepository;
    private final AmountConfigurationRepository amountConfigurationRepository;
    private final DrawableFragmentItem item;

    /* default */ PaymentMethodPresenter(@NonNull final PayerCostSelectionRepository payerCostSelectionRepository,
        @NonNull final AmountConfigurationRepository amountConfigurationRepository,
        @NonNull final DrawableFragmentItem item, @NonNull final MPTracker tracker) {
        super(tracker);
        this.payerCostSelectionRepository = payerCostSelectionRepository;
        this.amountConfigurationRepository = amountConfigurationRepository;
        this.item = item;
    }

    @Nullable
    private String getHighlightText() {
        final String customOptionId = item.getCommonsByApplication().getCurrent().getCustomOptionId();
        final int payerCostIndex = payerCostSelectionRepository.get(customOptionId);
        final AmountConfiguration configuration =
            amountConfigurationRepository.getConfigurationSelectedFor(customOptionId);
        final int installments = configuration == null || configuration.getPayerCosts().isEmpty() ?
            -1 : configuration.getCurrentPayerCost(false, payerCostIndex).getInstallments();
        final boolean hasReimbursement =
            item.getReimbursement() != null && item.getReimbursement().hasAppliedInstallment(installments);
        final String reimbursementMessage = hasReimbursement ? item.getReimbursement().getCard().getMessage() : null;
        final String chargeMessage = item.getCommonsByApplication().getCurrent().getChargeMessage();
        return chargeMessage != null ? chargeMessage : reimbursementMessage;
    }

    @Override
    public void onFocusIn() {
        if (item.shouldHighlightBottomDescription()) {
            getView().updateHighlightText(getHighlightText());
            getView().animateHighlightMessageIn();
        }
    }

    @Override
    public void onFocusOut() {
        if (item.shouldHighlightBottomDescription()) {
            getView().animateHighlightMessageOut();
        }
    }

    @Override
    public void onApplicationChanged(@NonNull final String paymentTypeId) {
        getTracker().track(new ComboSwitchEvent(paymentTypeId));
        item.getCommonsByApplication().update(paymentTypeId);
        onFocusOut();
        getView().updateView();
        getView().updateState();
    }

    @Override
    public void onTrackActivityNotFoundFriction(@NonNull final MercadoPagoError error) {
        track(FrictionEventTracker.with(
            "${TrackWrapper.BASE_PATH}/post_payment_deep_link",
            FrictionEventTracker.Id.INVALID_POST_PAYMENT_DEEP_LINK,
            FrictionEventTracker.Style.SCREEN,
            error
        ));
    }
}