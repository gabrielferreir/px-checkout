package com.mercadopago.android.px.internal.util

import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.preferences.CheckoutPreference

/**
 * Class used to avoid JvmName annotation on payemntProcessorV2
 */
internal object PaymentConfigurationUtil {
    @JvmStatic
    fun getPaymentProcessor(paymentConfiguration: PaymentConfiguration) = paymentConfiguration.paymentProcessorV2

    @JvmStatic
    fun hasPaymentProcessor(paymentConfiguration: PaymentConfiguration) = paymentConfiguration.hasPaymentProcessor()

    @JvmStatic
    fun getSupportsSplit(
        paymentConfiguration: PaymentConfiguration,
        checkoutPreference: CheckoutPreference?
    ) = paymentConfiguration.getSupportsSplit(checkoutPreference)
}