package com.mercadopago.android.px.internal.features.payment_result.props;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.view.PaymentResultMethod;
import java.util.List;

public class BodyErrorProps {

    public final String status;
    public final String statusDetail;
    public final String paymentMethodName;
    public final String paymentAmount;
    public final List<PaymentResultMethod.Model> paymentResultMethodModels;

    public BodyErrorProps(@NonNull final Builder builder) {
        status = builder.status;
        statusDetail = builder.statusDetail;
        paymentMethodName = builder.paymentMethodName;
        paymentAmount = builder.paymentAmount;
        paymentResultMethodModels = builder.paymentResultMethodModels;
    }

    public Builder toBuilder() {
        return new Builder()
            .setStatus(status)
            .setStatusDetail(statusDetail)
            .setPaymentMethodName(paymentMethodName)
            .setPaymentAmount(paymentAmount);
    }

    public static class Builder {

        public String status;
        public String statusDetail;
        public String paymentMethodName;
        public String paymentAmount;
        public List<PaymentResultMethod.Model> paymentResultMethodModels;

        public Builder setStatus(@NonNull final String status) {
            this.status = status;
            return this;
        }

        public Builder setStatusDetail(@NonNull final String statusDetail) {
            this.statusDetail = statusDetail;
            return this;
        }

        public Builder setPaymentMethodName(final String paymentMethodName) {
            this.paymentMethodName = paymentMethodName;
            return this;
        }

        public Builder setPaymentAmount(final String paymentAmount) {
            this.paymentAmount = paymentAmount;
            return this;
        }

        public Builder setPaymentResultMethodModels(
            @NonNull final List<PaymentResultMethod.Model> paymentResultMethodModels
        ) {
            this.paymentResultMethodModels = paymentResultMethodModels;
            return this;
        }

        public BodyErrorProps build() {
            return new BodyErrorProps(this);
        }
    }
}
