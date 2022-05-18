package com.mercadopago.android.px.core.internal;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.core.SplitPaymentProcessor;
import com.mercadopago.android.px.internal.mappers.Mapper;

public class CheckoutDataMapper extends Mapper<SplitPaymentProcessor.CheckoutData, PaymentProcessor.CheckoutData> {
    @Override
    public PaymentProcessor.CheckoutData map(@NonNull final SplitPaymentProcessor.CheckoutData val) {
        return new PaymentProcessor.CheckoutData(val.paymentDataList.get(0), val.checkoutPreference, val.securityType);
    }
}