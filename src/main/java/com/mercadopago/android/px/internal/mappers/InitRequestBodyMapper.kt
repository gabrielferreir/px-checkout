package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.internal.extensions.isZero
import com.mercadopago.android.px.internal.features.FeatureProvider
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.model.internal.InitRequestBody
import com.mercadopago.android.px.model.internal.PaymentTypeChargeRuleDM
import com.mercadopago.android.px.model.internal.DiscountParamsConfigurationDM
import com.mercadopago.android.px.model.internal.CheckoutFeaturesDM
import com.mercadopago.android.px.preferences.CheckoutPreference

internal class InitRequestBodyMapper (
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val featureProvider: FeatureProvider,
    private val paymentMethodBehaviourMapper: PaymentMethodBehaviourDMMapper,
    private val trackingRepository: TrackingRepository
) {
    fun map(checkout: MercadoPagoCheckout): InitRequestBody {
        return map(
            checkout.publicKey,
            checkout.paymentConfiguration,
            checkout.advancedConfiguration,
            checkout.preferenceId,
            checkout.checkoutPreference,
            null,
            null
        )
    }

    fun map(paymentSettingRepository: PaymentSettingRepository, cardId: String?, bankAccountId: String?): InitRequestBody {
        return map(
            paymentSettingRepository.publicKey,
            paymentSettingRepository.paymentConfiguration,
            paymentSettingRepository.advancedConfiguration,
            paymentSettingRepository.checkoutPreferenceId,
            paymentSettingRepository.checkoutPreference,
            cardId,
            bankAccountId
        )
    }

    private fun map(
        publicKey: String,
        paymentConfiguration: PaymentConfiguration,
        advancedConfiguration : AdvancedConfiguration,
        checkoutPreferenceId: String?,
        checkoutPreference: CheckoutPreference?,
        cardId: String?,
        bankAccountId: String?
    ): InitRequestBody {
        val features = featureProvider.availableFeatures
        val paymentMethodBehaviours = paymentMethodBehaviourMapper.map(advancedConfiguration.paymentMethodBehaviours)
        return InitRequestBody(
            publicKey,
            escManagerBehaviour.escCardIds,
            paymentConfiguration.charges.map {
                PaymentTypeChargeRuleDM(
                    it.paymentTypeId,
                    it.charge(),
                    if (it.charge().isZero()) it.message else it.label,
                    it.taxable
                )
            },
            DiscountParamsConfigurationDM(
                advancedConfiguration.discountParamsConfiguration.labels,
                advancedConfiguration.discountParamsConfiguration.productId,
                advancedConfiguration.discountParamsConfiguration.additionalParams
            ),
            CheckoutFeaturesDM(
                features.express, features.split, features.odrFlag, features.comboCard, features.hybridCard,
                features.pix, features.customTaxesCharges, features.cardsCustomTaxesCharges, features.taxableCharges,
                features.styleVersion, features.threedsSdkVersion, features.validationPrograms, features.debin,
                features.newPaymentMethodVersion
            ),
            paymentMethodBehaviours,
            paymentConfiguration.getCheckoutType(),
            checkoutPreferenceId,
            checkoutPreference,
            trackingRepository.flowId,
            cardId,
            bankAccountId,
            advancedConfiguration.paymentMethodRuleSet
        )
    }
}
