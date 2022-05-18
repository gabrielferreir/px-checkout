package com.mercadopago.android.px.internal.features.payment_result.viewmodel;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.view.PaymentResultMethod;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import java.util.List;

public class PaymentResultLegacyViewModel {

    public final PaymentModel model;
    public final PaymentResultScreenConfiguration configuration;
    public final List<PaymentResultMethod.Model> paymentResultMethodModels;

    public PaymentResultLegacyViewModel(
        @NonNull final PaymentModel model,
        @NonNull final List<PaymentResultMethod.Model> paymentResultMethodModels,
        @NonNull final PaymentResultScreenConfiguration configuration
    ) {
        this.model = model;
        this.paymentResultMethodModels = paymentResultMethodModels;
        this.configuration = configuration;
    }
}
