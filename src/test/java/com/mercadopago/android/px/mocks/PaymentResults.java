package com.mercadopago.android.px.mocks;

import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.utils.ResourcesUtil;

import static com.mercadopago.android.px.model.Payment.StatusCodes.STATUS_APPROVED;
import static com.mercadopago.android.px.model.Payment.StatusCodes.STATUS_IN_PROCESS;
import static com.mercadopago.android.px.model.Payment.StatusCodes.STATUS_PENDING;
import static com.mercadopago.android.px.model.Payment.StatusCodes.STATUS_REJECTED;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_ACCREDITED;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BLACKLIST;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_FRAUD;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_PENDING_REVIEW_MANUAL;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_BY_REGULATIONS;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_CAP_EXCEEDED;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_INSUFFICIENT_AMOUNT;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA;

public final class PaymentResults {

    private static final Long MOCKED_PAYMENT_ID = 8228734L;
    private static final Long MOCKED_BOLETO_PAYMENT_ID = 3109540394L;
    private static final String CPF_PATH = "payment_data_cpf.json";

    private PaymentResults() {

    }

    private static PaymentResult getGenericStatusPaymentResult(final String status, final String statusDetail) {
        return new PaymentResult.Builder()
            .setPaymentStatus(status)
            .setPaymentStatusDetail(statusDetail)
            .setPaymentData(getMockedPaymentData(null))
            .setPaymentId(MOCKED_PAYMENT_ID)
            .build();
    }

    private static PaymentResult getBoletoPaymentResult(final String status, final String detail) {
        return new PaymentResult.Builder()
            .setPaymentStatus(status)
            .setPaymentStatusDetail(detail)
            .setPaymentData(getMockedPaymentData(CPF_PATH))
            .setPaymentId(MOCKED_BOLETO_PAYMENT_ID)
            .build();
    }

    private static PaymentData getMockedPaymentData(@Nullable final String jsonPath) {
        final String json = ResourcesUtil.getStringResource(jsonPath == null ? "payment_data.json" : jsonPath);
        return JsonUtil.fromJson(json, PaymentData.class);
    }

    public static PaymentResult getStatusApprovedPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_APPROVED, STATUS_DETAIL_ACCREDITED);
    }

    public static PaymentResult getStatusInProcessContingencyPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_IN_PROCESS, STATUS_DETAIL_PENDING_CONTINGENCY);
    }

    public static PaymentResult getStatusInProcessReviewManualPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_IN_PROCESS, STATUS_DETAIL_PENDING_REVIEW_MANUAL);
    }

    public static PaymentResult getStatusCallForAuthPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);
    }

    public static PaymentResult getStatusRejectedCreditCardInsufficientAmountPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT);
    }

    public static PaymentResult getStatusRejectedInsufficientAmountPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_REJECTED_INSUFFICIENT_AMOUNT);
    }

    public static PaymentResult getStatusRejectedBadFilledSecuPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE);
    }

    public static PaymentResult getStatusRejectedBadFilledDatePaymentResult() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);
    }

    public static PaymentResult getStatusRejectedBadFilledFormPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER);
    }

    public static PaymentResult getStatusRejectedOtherPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_OTHER_REASON);

    }

    public static PaymentResult getStatusRejectedDuplicatedPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT);
    }

    public static PaymentResult getStatusRejectedMaxAttemptsPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS);
    }

    public static PaymentResult getPaymentMethodOffPaymentResult() {
        return getGenericStatusPaymentResult(STATUS_PENDING, STATUS_DETAIL_PENDING_WAITING_PAYMENT);
    }

    public static PaymentResult getBoletoApprovedPaymentResult() {
        return getBoletoPaymentResult(STATUS_PENDING, STATUS_DETAIL_PENDING_WAITING_PAYMENT);
    }

    public static PaymentResult getBoletoRejectedPaymentResult() {
        return getBoletoPaymentResult(STATUS_REJECTED, STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA);
    }

    public static PaymentResult getStatusRejectedBlacklist() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_BLACKLIST);
    }

    public static PaymentResult getStatusRejectedFraud() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_FRAUD);
    }

    public static PaymentResult getStatusRejectedCardDisabled() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_CC_REJECTED_CARD_DISABLED);
    }

    public static PaymentResult getStatusRejectedHighRisk() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_REJECTED_HIGH_RISK);
    }

    public static PaymentResult getStatusRejectedByRegulations() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_REJECTED_BY_REGULATIONS);
    }

    public static PaymentResult getStatusRejectedUnknown() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, "sarasa");
    }

    public static PaymentResult getStatusRejectedCapExceeded() {
        return getGenericStatusPaymentResult(STATUS_REJECTED, STATUS_DETAIL_REJECTED_CAP_EXCEEDED);
    }
}