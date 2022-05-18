package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import java.math.BigDecimal

//Should be internal but it's used in PaymentTypeChargeRule
interface ChargeRepository {
    val customCharges: List<PaymentTypeChargeRule>
    fun getChargeAmount(paymentTypeId: String): BigDecimal
    fun getChargeRule(paymentTypeId: String): PaymentTypeChargeRule?
    fun configure(customCharges: List<PaymentTypeChargeRule>)
    fun reset()
}