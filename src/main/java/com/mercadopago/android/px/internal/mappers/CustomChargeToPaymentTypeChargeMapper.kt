package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import com.mercadopago.android.px.model.internal.CustomChargeDM
import java.math.BigDecimal

internal class CustomChargeToPaymentTypeChargeMapper(
    val paymentConfiguration: PaymentConfiguration
) : Mapper<Map<String, CustomChargeDM>, ArrayList<PaymentTypeChargeRule>>() {

    override fun map(value: Map<String, CustomChargeDM>): ArrayList<PaymentTypeChargeRule> {
        val charges = ArrayList<PaymentTypeChargeRule>()
        for (customChargeEntry in value) {
            val paymentType = customChargeEntry.key
            val customCharge = customChargeEntry.value
            val existingCharge = paymentConfiguration.charges.firstOrNull { it.paymentTypeId == paymentType }
            charges.add(buildPaymentTypeChargeRule(customCharge, paymentType, existingCharge))
        }
        return charges
    }

    private fun buildPaymentTypeChargeRule(
        customCharge: CustomChargeDM,
        paymentType: String,
        existingCharge: PaymentTypeChargeRule?
    ) = if (isChargeFree(customCharge)) {
        PaymentTypeChargeRule.createChargeFreeRule(paymentType, existingCharge?.message.orEmpty())
    } else {
        PaymentTypeChargeRule(
            paymentType,
            customCharge.charge,
            existingCharge?.detailModal,
            customCharge.label
        )
    }

    private fun isChargeFree(customCharge: CustomChargeDM) =
        customCharge.charge.compareTo(BigDecimal.ZERO) == 0
}