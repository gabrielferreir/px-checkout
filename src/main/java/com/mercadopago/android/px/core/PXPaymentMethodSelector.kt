package com.mercadopago.android.px.core

import android.app.Activity
import com.mercadopago.android.px.configuration.PaymentMethodRules
import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.configuration.DynamicDialogConfiguration
import com.mercadopago.android.px.configuration.DiscountParamsConfiguration
import com.mercadopago.android.px.configuration.PaymentMethodBehaviour
import com.mercadopago.android.px.configuration.TrackingConfiguration
import com.mercadopago.android.px.configuration.CustomStringConfiguration
import com.mercadopago.android.px.internal.core.ProductIdProvider
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule

class PXPaymentMethodSelector private constructor(builder: Builder) {

    val accessToken = builder.accessToken
    val publicKey = builder.publicKey
    val preferenceId = builder.preferenceId
    val dynamicDialogConfiguration = builder.dynamicDialogConfiguration
    val customStringConfiguration = builder.customStringConfiguration
    val discountParamsConfiguration = builder.discountParamsConfiguration
    val trackingConfiguration = builder.trackingConfiguration
    val acceptThirdPartyCard = builder.acceptThirdPartyCard
    val charges = builder.charges
    val productId = builder.productId
    val supportSplit = builder.supportSplit
    val paymentMethodRuleSet = builder.paymentMethodRuleSet
    val paymentMethodBehaviours = builder.paymentMethodBehaviours

    fun start(activity: Activity, requestCode: Int) {
        val advancedConfiguration = AdvancedConfiguration
            .Builder()
            .setAcceptThirdPartyCard(acceptThirdPartyCard)
            .setCustomStringConfiguration(customStringConfiguration)
            .setDiscountParamsConfiguration(discountParamsConfiguration)
            .setDynamicDialogConfiguration(dynamicDialogConfiguration)
            .setPaymentMethodRuleSet(paymentMethodRuleSet)
            .setProductId(productId)
            .setPaymentMethodBehaviours(paymentMethodBehaviours)
            .build()

        val paymentProcessor = PaymentConfiguration
            .Builder()
            .addChargeRules(charges)
            .setSupportSplit(supportSplit)
            .build()

        MercadoPagoCheckout.Builder(publicKey, preferenceId, paymentProcessor)
            .setAdvancedConfiguration(advancedConfiguration)
            .setPrivateKey(accessToken)
            .setTrackingConfiguration(trackingConfiguration)
            .build()
            .startPayment(activity, requestCode)
    }

    class Builder(val publicKey: String, val preferenceId: String) {

        internal lateinit var accessToken: String
        internal var dynamicDialogConfiguration = DynamicDialogConfiguration.Builder().build()
        internal var customStringConfiguration = CustomStringConfiguration.Builder().build()
        internal var discountParamsConfiguration = DiscountParamsConfiguration.Builder().build()
        internal var trackingConfiguration = TrackingConfiguration.Builder().build()
        internal var acceptThirdPartyCard = false
        internal var charges: List<PaymentTypeChargeRule> = emptyList()
        internal var paymentMethodBehaviours: List<PaymentMethodBehaviour> = emptyList()
        internal var productId: String = ProductIdProvider.DEFAULT_PRODUCT_ID
        internal var supportSplit: Boolean = false
        internal var paymentMethodRuleSet: List<String> = emptyList()

        /**
         * Private key provides save card capabilities and account money balance.
         *
         * @param accessToken the user private key
         * @return builder to keep operating
         */
        fun setAccessToken(accessToken: String) = apply { this.accessToken = accessToken }

        /**
         * This provides a way to tell us what checks to apply to payment methods.
         *
         * @param paymentMethodRules the rule set
         * @return builder to keep operating
         */
        fun setPaymentMethodRules(@PaymentMethodRules paymentMethodRules: List<String>) = apply {
            this.paymentMethodRuleSet = paymentMethodRules
        }

        /**
         * It provides support for custom checkout functionality/ configure special behaviour You can enable/disable
         * several functionality.
         *
         * @param dynamicDialogConfiguration your configuration.
         * @return builder to keep operating
         */
        fun setDynamicDialogConfiguration(dynamicDialogConfiguration: DynamicDialogConfiguration) = apply {
            this.dynamicDialogConfiguration = dynamicDialogConfiguration
        }

        /**
         * It provides support for custom checkout functionality/ configure special behaviour You can enable/disable
         * several functionality.
         *
         * @param customStringConfiguration your configuration.
         * @return builder to keep operating
         */
        fun setCustomStringConfiguration(customStringConfiguration: CustomStringConfiguration) = apply {
            this.customStringConfiguration = customStringConfiguration
        }

        /**
         * It provides support for custom checkout functionality/ configure special behaviour You can enable/disable
         * several functionality.
         *
         * @param discountParamsConfiguration your configuration.
         * @return builder to keep operating
         */
        fun setDiscountParamsConfiguration(discountParamsConfiguration: DiscountParamsConfiguration) = apply {
            this.discountParamsConfiguration = discountParamsConfiguration
        }

        /**
         * It provides support for third party card
         *
         * @param supportThirdPartyCard your configuration.
         * @return builder to keep operating
         */
        fun setSupportThirdPartyCard(supportThirdPartyCard: Boolean) = apply {
            this.acceptThirdPartyCard = supportThirdPartyCard
        }

        /**
         * It provides support for custom checkout functionality/ configure special behaviour You can enable/disable
         * several functionality.
         *
         * @param productId your configuration.
         * @return builder to keep operating
         */
        fun setProductId(productId: String) = apply {
            this.productId = productId
        }

        /**
         * Add extra charges that will apply to total amount.
         *
         * @param charges the list of charges that could apply.
         * @return builder to keep operating
         */
        fun setChargeRules(charges: List<PaymentTypeChargeRule>) = apply {
            this.charges = charges
        }

        /**
         * It provides additional configurations to modify tracking and session data.
         *
         * @param trackingConfiguration your configuration.
         * @return builder to keep operating
         */
        fun setTrackingConfiguration(trackingConfiguration: TrackingConfiguration) = apply {
            this.trackingConfiguration = trackingConfiguration
        }

        /**
         * Provides additional settings to modify the types of payment methods to display at checkout.
         *
         * @param paymentMethodBehaviours your configuration.
         * @return builder to keep operating
         */
        fun setPaymentMethodBehaviours(paymentMethodBehaviours: List<PaymentMethodBehaviour>) = apply {
            this.paymentMethodBehaviours = paymentMethodBehaviours
        }

        /**
         * Method used as a flag to know if we should offer split payment method or not to the user.
         *
         * @return if it should show view
         */
        fun setSupportSplit(supportSplit: Boolean) = apply {
            this.supportSplit = supportSplit
        }

        /**
         * @return [PXPaymentMethodSelector] instance
         */
        fun build() : PXPaymentMethodSelector {
            check(accessToken != null) { "Access token is required" }
            return PXPaymentMethodSelector(this)
        }
    }
}