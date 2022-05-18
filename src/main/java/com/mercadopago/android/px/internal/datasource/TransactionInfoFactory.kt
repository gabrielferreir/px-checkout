package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.model.PaymentMethod
import com.mercadopago.android.px.model.PaymentMethods
import com.mercadopago.android.px.model.TransactionInfo
import com.mercadopago.android.px.model.BankInfo

internal class TransactionInfoFactory(private val payerPaymentMethodRepository: PayerPaymentMethodRepository) {

    fun create(customOptionId: String, paymentMethod: PaymentMethod): TransactionInfo {
        var bankInfo: BankInfo? = null
        val accountId = customOptionId.let { payerPaymentMethodRepository[it] }?.id
        val financialInstitutionId = paymentMethod.financialInstitutions?.firstOrNull()?.id

        if (paymentMethod.id == PaymentMethods.ARGENTINA.DEBIN) {
            check(!accountId.isNullOrEmpty()) { "account id should not be empty for debin" }
            check(!financialInstitutionId.isNullOrEmpty()) { "financial institution id should not be empty for debin" }

            bankInfo = BankInfo(accountId)
        }

        return TransactionInfo(bankInfo, financialInstitutionId)
    }
}
