package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.model.AmountConfiguration

internal class ConfigurationSolverImpl(
    private val payerPaymentMethodRepository: PayerPaymentMethodRepository) : ConfigurationSolver {

    override fun getAmountConfigurationSelectedFor(customOptionId: String): AmountConfiguration? {
        return payerPaymentMethodRepository[customOptionId]
            ?.let { it.getAmountConfiguration(it.defaultAmountConfiguration) }
    }

    override fun getConfigurationHashSelectedFor(customOptionId: String): String {
        return payerPaymentMethodRepository[customOptionId]?.defaultAmountConfiguration ?: TextUtil.EMPTY
    }

    override fun getAmountConfigurationFor(key: PayerPaymentMethodKey): AmountConfiguration? {
        return payerPaymentMethodRepository[key]?.let { it.getAmountConfiguration(it.defaultAmountConfiguration) }
    }

    override fun getConfigurationHashFor(key: PayerPaymentMethodKey): String {
        return payerPaymentMethodRepository[key]?.defaultAmountConfiguration ?: TextUtil.EMPTY
    }
}