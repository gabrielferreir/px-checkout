package com.mercadopago.android.px.internal.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.Collection;
import java.util.List;

public final class EscUtil {

    private EscUtil() {
    }

    private static boolean hasValidParametersForESC(@Nullable final PaymentData paymentData,
        @Nullable final String paymentStatus, @Nullable final String paymentDetail) {
        return paymentData != null && paymentData.containsCardInfo()
            && !TextUtil.isEmpty(paymentStatus)
            && !TextUtil.isEmpty(paymentDetail);
    }

    public static boolean shouldDeleteEsc(@NonNull final Collection<String> escBlacklistedStatus,
        @Nullable final PaymentData paymentData, @Nullable final String paymentStatus,
        @Nullable final String paymentDetail) {
        return hasValidParametersForESC(paymentData, paymentStatus, paymentDetail) &&
            ListUtil.contains(escBlacklistedStatus, paymentDetail, ListUtil.CONTAIN_IGNORE_CASE);
    }

    public static boolean shouldStoreESC(@NonNull final Collection<String> escBlacklistedStatus,
        @Nullable final PaymentData paymentData, @Nullable final String paymentStatus,
        @Nullable final String paymentDetail) {
        return hasValidParametersForESC(paymentData, paymentStatus, paymentDetail) &&
            !ListUtil.contains(escBlacklistedStatus, paymentDetail, ListUtil.CONTAIN_IGNORE_CASE) &&
            !TextUtil.isEmpty(paymentData.getToken().getEsc());
    }

    public static boolean isInvalidEscPayment(@Nullable final PaymentData paymentData,
        @Nullable final String paymentStatus, @Nullable final String paymentDetail) {
        return hasValidParametersForESC(paymentData, paymentStatus, paymentDetail) &&
            Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC.equals(paymentDetail);
    }

    public static boolean isErrorInvalidPaymentWithEsc(final MercadoPagoError error) {
        if (error.isApiException() && error.getApiException().getStatus() == ApiUtil.StatusCodes.BAD_REQUEST) {
            final List<Cause> causes = error.getApiException().getCause();
            if (causes != null) {
                boolean isInvalidEsc = false;
                for (final Cause cause : causes) {
                    isInvalidEsc |= ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC.equals(cause.getCode());
                }
                return isInvalidEsc;
            }
        }
        return false;
    }
}