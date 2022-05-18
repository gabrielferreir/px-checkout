package com.mercadopago.android.px.internal.viewmodel.handlers

import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel

interface PaymentModelHandler {

    fun visit(paymentModel: PaymentModel)

    fun visit(businessPaymentModel: BusinessPaymentModel)
}