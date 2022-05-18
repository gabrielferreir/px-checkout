package com.mercadopago.android.px.internal.features.payment_congrats.mapper

import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.BusinessPayment

internal object PaymentCongratsTypeMapper : Mapper<BusinessPayment.Decorator, PaymentCongratsModel.CongratsType>() {

    override fun map(value: BusinessPayment.Decorator): PaymentCongratsModel.CongratsType {
        return when (value) {
            BusinessPayment.Decorator.APPROVED -> PaymentCongratsModel.CongratsType.APPROVED
            BusinessPayment.Decorator.REJECTED -> PaymentCongratsModel.CongratsType.REJECTED
            BusinessPayment.Decorator.PENDING -> PaymentCongratsModel.CongratsType.PENDING
        }
    }
}
