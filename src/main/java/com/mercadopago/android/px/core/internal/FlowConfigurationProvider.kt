package com.mercadopago.android.px.core.internal

import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.SelectorConfirmButtonFragment
import com.mercadopago.android.px.internal.features.pay_button.PayButtonFragment
import com.mercadopago.android.px.internal.viewmodel.FlowConfigurationModel

internal class FlowConfigurationProvider(private val paymentConfiguration: PaymentConfiguration) {
    fun getFlowConfiguration(): FlowConfigurationModel {
        val confirmButton = if (paymentConfiguration.hasPaymentProcessor()) {
            PayButtonFragment()
        } else {
            SelectorConfirmButtonFragment()
        }
        return FlowConfigurationModel(confirmButton)
    }
}
