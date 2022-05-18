package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo
import com.mercadopago.android.px.internal.view.PaymentResultAmount

internal object PaymentResultAmountMapper : Mapper<PaymentInfo, PaymentResultAmount.Model>() {

    override fun map(value: PaymentInfo): PaymentResultAmount.Model = value.run {
        PaymentResultAmount.Model.Builder(paidAmount, rawAmount)
            .setDiscountName(discountName)
            .setNumberOfInstallments(installmentsCount)
            .setInstallmentsAmount(installmentsAmount)
            .setInstallmentsRate(installmentsRate)
            .setInstallmentsTotalAmount(installmentsTotalAmount)
            .build()
    }
}
