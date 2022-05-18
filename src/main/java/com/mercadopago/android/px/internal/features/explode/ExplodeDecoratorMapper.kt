package com.mercadopago.android.px.internal.features.explode

import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentResultType
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.BusinessPayment

internal class ExplodeDecoratorMapper(
    private val factory: PaymentResultViewModelFactory) : Mapper<PaymentModel, ExplodeDecorator>() {
    override fun map(model: PaymentModel): ExplodeDecorator {
        val payment = model.payment
        return when {
            model.remedies.hasRemedies() -> ExplodeDecorator.from(RemediesModel.DECORATOR)
            payment is BusinessPayment -> ExplodeDecorator.from(PaymentResultType.from(payment.decorator))
            else -> {
                val decorator = factory.createPaymentResultDecorator(model.paymentResult)
                ExplodeDecorator(decorator.primaryColor, decorator.statusIcon)
            }
        }
    }
}
