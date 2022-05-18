package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.addons.model.PaymentMethodReauthModel
import com.mercadopago.android.px.model.PaymentData

internal class PaymentMethodReauthMapper {
    fun map(paymentMethods: List<PaymentData>): List<PaymentMethodReauthModel> {
        return paymentMethods.map {
            PaymentMethodReauthModel(
                it.rawAmount,
                it.paymentMethod.id,
                it.paymentMethod.paymentTypeId
            )
        }
    }
}
