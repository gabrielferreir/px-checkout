package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.extensions.isNotNull
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey
import com.mercadopago.android.px.model.Payment
import com.mercadopago.android.px.model.PaymentResult
import com.mercadopago.android.px.model.PaymentTypes.isCardPaymentType
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod
import java.io.File

private const val DISABLED_PAYMENT_METHOD = "disabled_payment_method_repository"

internal class DisabledPaymentMethodRepositoryImpl(private val fileManager: FileManager) :
    AbstractLocalRepository<MutableMap<PayerPaymentMethodKey, DisabledPaymentMethod>>(fileManager),
    DisabledPaymentMethodRepository {

    override val file: File = fileManager.create(DISABLED_PAYMENT_METHOD)

    override fun get(key: PayerPaymentMethodKey) = value[key]

    private fun set(key: PayerPaymentMethodKey, disabledPaymentMethod: DisabledPaymentMethod) {
        value[key] = disabledPaymentMethod
        configure(value)
    }

    override fun hasKey(key: PayerPaymentMethodKey) = value[key].isNotNull()

    override fun handleRejectedPayment(paymentResult: PaymentResult) {
        paymentResult.takeIf { shouldDisable(it) }?.let {
            val isSplit = it.paymentDataList.size > 1
            val paymentTypeId = it.paymentData.paymentMethod.paymentTypeId
            val cardId = it.paymentData.token?.cardId

            val (payerPaymentMethodId, paymentMethodId) = when {
                isSplit && it.paymentMethodId.isNotNullNorEmpty() -> {
                    it.paymentMethodId to it.paymentMethodId
                }
                isCardPaymentType(it.paymentData.paymentMethod.paymentTypeId) && cardId.isNotNullNorEmpty() -> {
                    cardId to it.paymentData.paymentMethod.id
                }
                else -> it.paymentData.paymentMethod.id to it.paymentData.paymentMethod.id
            }

            if (!isCardPaymentType(paymentTypeId) || cardId.isNotNullNorEmpty()) {
                set(PayerPaymentMethodKey(payerPaymentMethodId, paymentTypeId),
                    DisabledPaymentMethod(paymentMethodId, paymentResult.paymentStatusDetail))
            }
        }
    }

    private fun shouldDisable(paymentResult: PaymentResult): Boolean {
        return Payment.StatusCodes.STATUS_REJECTED.equals(paymentResult.paymentStatus, ignoreCase = true) &&
            (Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK
                .equals(paymentResult.paymentStatusDetail, ignoreCase = true) ||
                Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_HIGH_RISK
                    .equals(paymentResult.paymentStatusDetail, ignoreCase = true) ||
                Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BLACKLIST
                    .equals(paymentResult.paymentStatusDetail, ignoreCase = true) ||
                Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT
                    .equals(paymentResult.paymentStatusDetail, ignoreCase = true))
    }

    override fun readFromStorage() =
        fileManager.readAnyMap(file, PayerPaymentMethodKey::class.java, DisabledPaymentMethod::class.java) as MutableMap
}