package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.PaymentResult

internal class PaymentResultFactory(private val paymentDataFactory: PaymentDataFactory) {

    /**
     * Create a PaymentResult with an IPaymentDescriptor
     *
     * @param payment The payment model
     * @return The transformed {@link PaymentResult}
     */
    fun create(payment: IPaymentDescriptor): PaymentResult {
        return PaymentResult.Builder()
            .setPaymentData(paymentDataFactory.create())
            .setPaymentId(payment.id)
            .setPaymentMethodId(payment.paymentMethodId)
            .setPaymentStatus(payment.paymentStatus)
            .setStatementDescription(payment.statementDescription)
            .setPaymentStatusDetail(payment.paymentStatusDetail)
            .build()
    }
}