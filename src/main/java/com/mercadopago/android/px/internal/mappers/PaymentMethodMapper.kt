package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.repository.PaymentMethodRepository
import com.mercadopago.android.px.model.PaymentMethod

internal class PaymentMethodMapper(
    private val paymentMethodRepository: PaymentMethodRepository
) : Mapper<Pair<String, String>, PaymentMethod>() {

    override fun map(value: Pair<String, String>): PaymentMethod {
        return paymentMethodRepository.getPaymentMethodById(value.first).also {
            it.paymentTypeId = value.second
        }
    }

}