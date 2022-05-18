package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.PaymentMethod

internal interface PaymentMethodRepository : LocalRepository<List<PaymentMethod>> {
    fun getPaymentMethodById(paymentMethodId: String): PaymentMethod
}