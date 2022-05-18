package com.mercadopago.android.px.tracking.internal

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.model.PaymentData

internal class BankInfoHelper(
    private val payerPaymentMethodRepository: PayerPaymentMethodRepository
) {
    fun getExternalAccountId(paymentData: PaymentData?) = paymentData?.transactionInfo?.bankInfo?.accountId

    fun getBankName(customOptionId: String?) = customOptionId?.let { payerPaymentMethodRepository[it]?.bankInfo?.name }

    fun getBankName(paymentData: PaymentData?) = getExternalAccountId(paymentData)?.let { payerPaymentMethodRepository[it]?.bankInfo?.name }
}
