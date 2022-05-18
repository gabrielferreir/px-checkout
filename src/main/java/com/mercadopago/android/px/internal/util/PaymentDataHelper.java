package com.mercadopago.android.px.internal.util;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo;
import com.mercadopago.android.px.model.PaymentData;
import java.math.BigDecimal;
import java.util.Collection;

public final class PaymentDataHelper {

    private PaymentDataHelper() {
    }

    public static BigDecimal getPrettyAmountToPay(@NonNull final PaymentData paymentData) {
        if (paymentData.getPayerCost() != null) {
            return paymentData.getPayerCost().getTotalAmount();
        } else if (paymentData.getDiscount() != null) {
            return paymentData.getNoDiscountAmount().subtract(paymentData.getDiscount().getCouponAmount());
        }
        return paymentData.getNoDiscountAmount();
    }

    public static boolean isSplitPaymentData(@NonNull final Collection<PaymentData> paymentDataList) {
        return paymentDataList.size() > 1;
    }

    public static boolean isSplitPaymentInfo(@NonNull final Collection<PaymentInfo> paymentDataList) {
        return paymentDataList.size() > 1;
    }

    @NonNull
    public static BigDecimal getTotalDiscountAmount(@NonNull final Iterable<PaymentData> paymentDataList) {
        BigDecimal totalDiscountAmount = BigDecimal.ZERO;

        for (final PaymentData paymentData : paymentDataList) {
            if (paymentData.getDiscount() != null) {
                totalDiscountAmount = totalDiscountAmount.add(paymentData.getDiscount().getCouponAmount());
            }
        }
        return totalDiscountAmount;
    }
}