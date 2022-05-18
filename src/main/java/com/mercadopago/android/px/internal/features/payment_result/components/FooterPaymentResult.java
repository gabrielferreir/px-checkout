package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Button;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.view.Footer;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.model.PaymentResult;

public class FooterPaymentResult extends CompactComponent<PaymentResult, ActionDispatcher> {

    @NonNull private final PaymentResultViewModelFactory factory;

    /* default */ FooterPaymentResult(@NonNull final PaymentResultViewModelFactory factory,
        @NonNull final PaymentResult props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
        this.factory = factory;
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        return new Footer(getFooterProps(parent.getContext()), getActions()).render(parent);
    }

    @VisibleForTesting
    Footer.Props getFooterProps(@NonNull final Context context) {
        Button.Props buttonAction = null;
        Button.Props linkAction = null;

        final PaymentResultViewModel paymentResultViewModel = factory.createPaymentResultViewModel(props);

        if (paymentResultViewModel.getLinkAction() != null) {
            linkAction = new Button.Props(paymentResultViewModel.getLinkActionTitle(context),
                paymentResultViewModel.getLinkAction());
        }

        if (paymentResultViewModel.getMainAction() != null) {
            buttonAction = new Button.Props(paymentResultViewModel.getMainActionTitle(context),
                paymentResultViewModel.getMainAction());
        }

        return new Footer.Props(buttonAction, linkAction);
    }
}
