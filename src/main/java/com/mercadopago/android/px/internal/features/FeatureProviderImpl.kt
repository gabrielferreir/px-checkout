package com.mercadopago.android.px.internal.features

import com.mercadopago.android.px.addons.TokenDeviceBehaviour
import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.core.internal.ConfigurationProvider
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.CheckoutFeatures
import com.mercadopago.android.px.preferences.CheckoutPreference

internal class FeatureProviderImpl(
    val configurationProvider: ConfigurationProvider,
    val tokenDeviceBehaviour: TokenDeviceBehaviour) : FeatureProvider {

    constructor(mercadoPagoCheckout: MercadoPagoCheckout, tokenDeviceBehaviour: TokenDeviceBehaviour) : this(
        object : ConfigurationProvider {
            override val checkoutPreference: CheckoutPreference?
                get() = mercadoPagoCheckout.checkoutPreference
            override val paymentConfiguration: PaymentConfiguration
                get() = mercadoPagoCheckout.paymentConfiguration
        }, tokenDeviceBehaviour
    )

    override val availableFeatures: CheckoutFeatures
        get() {
            val splitFeature = with(configurationProvider) {
                paymentConfiguration.getSupportsSplit(checkoutPreference)
            }
            val builder = CheckoutFeatures.Builder()
                .setSplit(splitFeature)
                .setExpress(true)
                .setOdrFlag(true)
                .setComboCard(true)
                .setHybridCard(true)
                .setPix(true)
                .setCustomTaxesCharges(true)
                .setCardsCustomTaxesCharges(true)
                .setTaxableCharges(true)
                .addValidationProgram(Application.KnownValidationProgram.STP.value)

            if (tokenDeviceBehaviour.isFeatureAvailable) {
                builder.addValidationProgram(Application.KnownValidationProgram.TOKEN_DEVICE.value)
            }

            return builder.build()
        }
}
