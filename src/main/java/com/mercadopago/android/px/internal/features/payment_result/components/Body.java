package com.mercadopago.android.px.internal.features.payment_result.components;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.features.payment_result.props.BodyErrorProps;
import com.mercadopago.android.px.internal.features.payment_result.props.PaymentResultBodyProps;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;
import com.mercadopago.android.px.internal.util.PaymentDataHelper;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultViewModel;

public class Body extends CompactComponent<PaymentResultBodyProps, ActionDispatcher> {

    @NonNull private final PaymentResultViewModelFactory factory;

    /* default */ Body(@NonNull final PaymentResultViewModelFactory factory,
        @NonNull final PaymentResultBodyProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
        this.factory = factory;
    }

    /* default */ boolean hasSomethingToDraw() {
        return hasBodyError();
    }

    private boolean hasBodyError() {
        final PaymentResultViewModel paymentResultViewModel = factory.createPaymentResultViewModel(props.paymentResult);
        return paymentResultViewModel.hasBodyError();
    }

    private BodyError getBodyErrorComponent() {
        final BodyErrorProps bodyErrorProps = new BodyErrorProps.Builder()
            .setStatus(props.paymentResult.getPaymentStatus())
            .setStatusDetail(props.paymentResult.getPaymentStatusDetail())
            .setPaymentMethodName(props.paymentResult.getPaymentData().getPaymentMethod().getName())
            .setPaymentResultMethodModels(props.paymentResultMethodModels)
            .setPaymentAmount(CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(props.currency,
                PaymentDataHelper.getPrettyAmountToPay(props.paymentResult.getPaymentData())))
            .build();
        return new BodyError(factory, bodyErrorProps, getActions());
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        if (hasBodyError()) {
            getBodyErrorComponent().render(parent);
        }
        return parent;
    }
}
