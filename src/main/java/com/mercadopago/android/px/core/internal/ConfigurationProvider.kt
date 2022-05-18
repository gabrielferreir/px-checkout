package com.mercadopago.android.px.core.internal

import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.preferences.CheckoutPreference

internal interface ConfigurationProvider {
    val checkoutPreference: CheckoutPreference?
    val paymentConfiguration: PaymentConfiguration
}