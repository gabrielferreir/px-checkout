package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.PaymentResult

internal interface CongratsRepository {
    fun getPostPaymentData(payment: IPaymentDescriptor, paymentResult: PaymentResult,
                           callback: PostPaymentCallback)

    interface PostPaymentCallback {
        fun handleResult(paymentModel: PaymentModel)
    }
}