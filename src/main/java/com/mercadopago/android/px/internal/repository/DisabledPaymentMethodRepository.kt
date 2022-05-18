package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.PaymentResult
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod

internal interface DisabledPaymentMethodRepository
    : LocalRepository<MutableMap<PayerPaymentMethodKey, DisabledPaymentMethod>> {

    operator fun get(key: PayerPaymentMethodKey): DisabledPaymentMethod?
    fun hasKey(key: PayerPaymentMethodKey): Boolean
    fun handleRejectedPayment(paymentResult: PaymentResult)
}