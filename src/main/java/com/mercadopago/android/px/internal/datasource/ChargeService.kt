package com.mercadopago.android.px.internal.datasource

import android.content.SharedPreferences
import com.mercadopago.android.px.internal.repository.ChargeRepository
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import java.math.BigDecimal

private const val PREF_CHARGES = "PREF_CHARGES"

internal class ChargeService(private val sharedPreferences: SharedPreferences) : ChargeRepository {
    private var internalCustomCharges: List<PaymentTypeChargeRule>? = null
    override val customCharges: List<PaymentTypeChargeRule>
        get() {
            if (internalCustomCharges == null) {
                internalCustomCharges = JsonUtil.getListFromJson(
                    sharedPreferences.getString(PREF_CHARGES, null), PaymentTypeChargeRule::class.java)
            }
            return internalCustomCharges ?: emptyList()
        }

    override fun getChargeAmount(paymentTypeId: String): BigDecimal {
        var chargeAmount = BigDecimal.ZERO
        customCharges.forEach {
            if (shouldApply(paymentTypeId, it)) {
                chargeAmount = chargeAmount.add(it.charge())
            }
        }
        return chargeAmount
    }

    override fun getChargeRule(paymentTypeId: String): PaymentTypeChargeRule? {
        return customCharges.firstOrNull {
            shouldApply(paymentTypeId, it)
        }
    }

    override fun configure(customCharges: List<PaymentTypeChargeRule>) {
        this.internalCustomCharges = customCharges
        sharedPreferences.edit().apply {
            putString(PREF_CHARGES, JsonUtil.toJson(customCharges))
            apply()
        }
    }

    override fun reset() {
        sharedPreferences.edit().remove(PREF_CHARGES).apply()
        internalCustomCharges = null
    }

    private fun shouldApply(paymentTypeId: String, rule: PaymentTypeChargeRule): Boolean {
        return rule.paymentTypeId.equals(paymentTypeId, ignoreCase = true)
    }
}
