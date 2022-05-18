package com.mercadopago.android.px.tracking.internal.model

import com.mercadopago.android.px.model.PaymentData

class PaymentDataExtraInfo private constructor(
    selectedInstallment: PayerCostInfo?,
    cardId: String?,
    hasEsc: Boolean,
    issuerId: Long?
) : TrackingMapModel() {

    companion object {
        @JvmStatic
        fun resultPaymentDataExtraInfo(paymentData: PaymentData): PaymentDataExtraInfo {
            val payerCost = paymentData.payerCost
            val token = paymentData.token
            val issuerId = paymentData.issuer?.id

            return PaymentDataExtraInfo(
                payerCost?.let { PayerCostInfo(it) },
                token?.cardId,
                token?.esc != null,
                issuerId)
        }
    }
}
