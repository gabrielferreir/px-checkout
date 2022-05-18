package com.mercadopago.android.px.tracking.internal;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel;
import com.mercadopago.android.px.internal.util.TextUtil;

public final class TrackingHelper {

    private TrackingHelper() {}

    public static String getPaymentStatus(@NonNull final PaymentCongratsModel model) {
        final String paymentStatus = model.getPxPaymentCongratsTracking().getPaymentStatus();
        if (TextUtil.isEmpty(paymentStatus)) {
            return model.getCongratsType().name;
        }
        return paymentStatus;
    }
}
