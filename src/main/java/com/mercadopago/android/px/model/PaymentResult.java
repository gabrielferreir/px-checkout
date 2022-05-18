package com.mercadopago.android.px.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import com.mercadopago.android.px.internal.util.StatusHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class PaymentResult implements Serializable {

    @Nullable private final Long paymentId;
    private final String paymentStatus;
    private final String paymentStatusDetail;
    private final String statementDescription;
    private final List<PaymentData> paymentDataList;
    private final String paymentMethodId;
    private final PaymentData paymentData;

    /* default */ PaymentResult(final Builder builder) {
        paymentData = builder.paymentData;
        paymentId = builder.paymentId;
        paymentStatus = builder.paymentStatus;
        paymentStatusDetail = builder.paymentStatusDetail;
        statementDescription = builder.statementDescription;
        paymentDataList = builder.paymentDataList;
        paymentMethodId = builder.paymentMethodId;
    }

    /**
     * when the payment result talks about multiple payment data (split payment) the semantic payment data represents
     * the payment method that will represents the payment itself. if there is an specific payment method that the
     * payment result talks about then the payment data returned will be that one.
     *
     * @return semantic payment data.
     */
    public PaymentData getPaymentData() {
        if (paymentDataList == null) {
            return paymentData;
        } else if (paymentMethodId != null) {
            // if you're paying with multiple payment methods, then
            // you must know witch one is the default one for the approve/rejected case.
            for (final PaymentData paymentData : paymentDataList) {
                if (paymentData.getPaymentMethod().getId().equals(paymentMethodId)) {
                    return paymentData;
                }
            }
        }
        // by default the first one is the paymentData old variable.
        return paymentData;
    }

    @NonNull
    public List<PaymentData> getPaymentDataList() {
        return paymentDataList;
    }

    @Nullable
    public Long getPaymentId() {
        return paymentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public boolean isApproved() {
        return Payment.StatusCodes.STATUS_APPROVED.equals(paymentStatus);
    }

    public boolean isStatusRejected() {
        return Payment.StatusCodes.STATUS_REJECTED.equals(paymentStatus);
    }

    public boolean isStatusPending() {
        return Payment.StatusCodes.STATUS_PENDING.equals(paymentStatus);
    }

    public boolean isStatusInProcess() {
        return Payment.StatusCodes.STATUS_IN_PROCESS.equals(paymentStatus);
    }

    /**
     * @return if status is pending and status detail pending_waiting_payment.
     */
    public boolean isOffPayment() {
        return paymentId != null && Payment.StatusCodes.STATUS_PENDING.equalsIgnoreCase(paymentStatus) &&
            StatusHelper.isPendingStatusDetailSuccess(paymentStatusDetail);
    }

    public String getPaymentStatusDetail() {
        return paymentStatusDetail;
    }

    public String getStatementDescription() {
        return statementDescription;
    }

    public boolean isCallForAuthorize() {
        return Payment.StatusCodes.STATUS_REJECTED.equals(getPaymentStatus()) &&
            Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE.equals(getPaymentStatusDetail());
    }

    public boolean isRejected() {
        return Payment.StatusCodes.STATUS_REJECTED.equals(getPaymentStatus());
    }

    //TODO rename method
    public boolean isInstructions() {
        return (Payment.StatusCodes.STATUS_PENDING.equals(getPaymentStatus()) ||
            Payment.StatusCodes.STATUS_IN_PROCESS.equals(getPaymentStatus())) &&
            StatusHelper.isPendingStatusDetailSuccess(getPaymentStatusDetail());
    }

    public boolean isPending() {
        return getPaymentStatus().equals(Payment.StatusCodes.STATUS_PENDING) ||
            getPaymentStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS);
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public static final class Builder {

        /* default */ PaymentData paymentData;
        /* default */ Long paymentId;
        /* default */ String paymentStatus;
        /* default */ String paymentStatusDetail;
        /* default */ String statementDescription;
        /* default */ List<PaymentData> paymentDataList = new ArrayList<>();
        /* default */ @Nullable String paymentMethodId;

        /**
         * use set payment data with a list value.
         *
         * @param paymentData
         * @return builder.
         */
        @Deprecated
        public Builder setPaymentData(@NonNull final PaymentData paymentData) {
            this.paymentData = paymentData;
            paymentDataList = Collections.singletonList(paymentData);
            return this;
        }

        public Builder setPaymentId(@Nullable final Long paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder setPaymentStatus(@NonNull final String paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public Builder setPaymentStatusDetail(@NonNull final String statusDetail) {
            paymentStatusDetail = statusDetail;
            return this;
        }

        public Builder setStatementDescription(@Nullable final String statementDescription) {
            this.statementDescription = statementDescription;
            return this;
        }

        public Builder setPaymentData(@NonNull @Size(min = 1) final List<PaymentData> paymentData) {
            setPaymentData(paymentData.get(0));
            paymentDataList = paymentData;
            return this;
        }

        public PaymentResult build() {
            return new PaymentResult(this);
        }

        public Builder setPaymentMethodId(final String paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
            return this;
        }
    }
}