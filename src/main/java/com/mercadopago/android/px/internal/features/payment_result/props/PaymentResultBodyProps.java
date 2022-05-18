package com.mercadopago.android.px.internal.features.payment_result.props;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.view.PaymentResultMethod;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.PaymentResult;
import java.util.Collections;
import java.util.List;

public class PaymentResultBodyProps {

    public final Currency currency;
    public final PaymentResultScreenConfiguration configuration;
    public final PaymentResult paymentResult;
    public final List<PaymentResultMethod.Model> paymentResultMethodModels;

    public PaymentResultBodyProps(@NonNull final Builder builder) {
        paymentResult = builder.paymentResult;
        currency = builder.currency;
        configuration = builder.configuration;
        paymentResultMethodModels = builder.paymentResultMethodModels;
    }

    public static class Builder {
        public Currency currency;
        public PaymentResultScreenConfiguration configuration;
        public PaymentResult paymentResult;
        public List<PaymentResultMethod.Model> paymentResultMethodModels = Collections.emptyList();

        public Builder(@NonNull final PaymentResultScreenConfiguration configuration) {
            this.configuration = configuration;
        }

        public Builder setCurrency(final Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder setPaymentResult(final PaymentResult paymentResult) {
            this.paymentResult = paymentResult;
            return this;
        }

        public Builder setPaymentResultMethodModels(
            @NonNull final List<PaymentResultMethod.Model> paymentResultMethodModels
        ) {
            this.paymentResultMethodModels = paymentResultMethodModels;
            return this;
        }

        public PaymentResultBodyProps build() {
            return new PaymentResultBodyProps(this);
        }
    }
}
