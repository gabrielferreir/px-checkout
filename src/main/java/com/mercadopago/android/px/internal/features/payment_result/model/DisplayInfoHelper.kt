package com.mercadopago.android.px.internal.features.payment_result.model

import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentResultInfo
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.CustomSearchItem
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.display_info.CustomSearchItemDisplayInfo.Result.PaymentMethod
import com.mercadopago.android.px.model.display_info.DisplayInfo

internal class DisplayInfoHelper(
    val payerPaymentMethodRepository: PayerPaymentMethodRepository,
    val userSelectionRepository: UserSelectionRepository
) {

    fun resolve(paymentData: PaymentData, paymentInfoBuilder: PaymentInfo.Builder) {
        val payerPaymentMethod = userSelectionRepository.customOptionId?.let { payerPaymentMethodRepository[it] }
        resolvePaymentMethodDisplayInfo(payerPaymentMethod, paymentData, paymentInfoBuilder)
        payerPaymentMethod?.displayInfo?.result?.extraInfo?.let {
            paymentInfoBuilder.withExtraInfo(it.detail.map { text ->
                PaymentCongratsText.from(text)
            })
        }
    }

    private fun resolvePaymentMethodDisplayInfo(
        payerPaymentMethod: CustomSearchItem?,
        paymentData: PaymentData,
        paymentInfoBuilder: PaymentInfo.Builder
    ) {
        if (payerPaymentMethod?.displayInfo?.result?.paymentMethod != null) {
            resolveCustomSearchItemPaymentMethodDisplayInfo(
                payerPaymentMethod.displayInfo!!.result.paymentMethod!!,
                paymentInfoBuilder
            )
        } else {
            resolveGenericPaymentMethodDisplayInfo(paymentData.paymentMethod.displayInfo, paymentInfoBuilder)
        }
    }

    private fun resolveGenericPaymentMethodDisplayInfo(
        displayInfo: DisplayInfo?,
        paymentInfoBuilder: PaymentInfo.Builder
    ) {
        displayInfo?.run {
            resultInfo?.let {
                paymentInfoBuilder.withConsumerCreditsInfo(PaymentResultInfo(it.title, it.subtitle))
            }
            description?.let {
                paymentInfoBuilder.withDescription(PaymentCongratsText.from(it))
            }
        }
    }

    private fun resolveCustomSearchItemPaymentMethodDisplayInfo(
        paymentMethod: PaymentMethod,
        paymentInfoBuilder: PaymentInfo.Builder
    ) {
        paymentInfoBuilder.withDetails(paymentMethod.detail.map {
            PaymentCongratsText.from(it)
        }).withIconUrl(paymentMethod.iconUrl)
    }
}
