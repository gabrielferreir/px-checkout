package com.mercadopago.android.px.utils

import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.PayerCost

object PayerCostUtils {
    fun getPayerCost(
        installments: Int,
        installmentRate: Int,
        installmentAmount: Int,
        totalAmount: Int = 120,
        interestRate: Int? = null
    ): PayerCost {
        var json = """{
            "installments": $installments,
            "labels": [
                "recommended_installment",
                "CFT_0,00%|TEA_0,00%"
            ],
            "installment_rate": $installmentRate,
            "total_amount": $totalAmount,
            "installment_amount": $installmentAmount,
            "min_allowed_amount": 1,
            "max_allowed_amount": 250000,
            "recommended_message": "3 cuotas de $ 40,00 ($ 120,00)"
        """

        if (interestRate != null) {
            json += """,
                "interest_rate":{
                "message":"CFTEA: $interestRate%",
                "text_color":"#999999",
                "weight":"regular"
            }"""
        }
        return JsonUtil.fromJson("$json }".trimIndent(), PayerCost::class.java)!!
    }
}