package com.mercadopago.android.px.internal.datasource;

import com.mercadopago.android.px.configuration.PaymentConfiguration;

public final class MercadoPagoPaymentConfiguration {

    private MercadoPagoPaymentConfiguration() {
    }

    public static PaymentConfiguration create() {
        return new PaymentConfiguration.Builder(new DefaultPaymentProcessor())
            .build();
    }
}