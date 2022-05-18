package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.core.internal.ConfigurationProvider
import com.mercadopago.android.px.internal.model.SecurityType
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.model.Site
import com.mercadopago.android.px.model.Token
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import com.mercadopago.android.px.model.internal.Configuration
import com.mercadopago.android.px.preferences.CheckoutPreference

internal interface PaymentSettingRepository : ConfigurationProvider {
    val checkoutPreferenceId: String?
    val publicKey: String
    val site: Site
    val currency: Currency
    val transactionId: String
    val advancedConfiguration: AdvancedConfiguration
    val configuration: Configuration
    val token: Token?
    val securityType: SecurityType
    val chargeRules: List<PaymentTypeChargeRule>
    fun hasToken(): Boolean
    fun configure(advancedConfiguration: AdvancedConfiguration)
    fun configure(publicKey: String)
    fun configure(site: Site)
    fun configure(currency: Currency)
    fun configure(checkoutPreference: CheckoutPreference?)
    fun configure(paymentConfiguration: PaymentConfiguration)
    fun configure(configuration: Configuration)
    fun configure(token: Token)
    fun configure(secondFactor: SecurityType)
    fun configurePreferenceId(preferenceId: String?)
    fun clearToken()
    fun reset()
}