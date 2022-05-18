package com.mercadopago.android.px.internal.mappers

import android.content.Context
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.commons.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.view.PaymentResultMethod
import com.mercadopago.android.px.model.PaymentTypes
import java.util.Locale

internal class PaymentResultMethodMapper(context: Context, private val paymentResultAmountMapper: PaymentResultAmountMapper) {
    private val context = context.applicationContext

    @JvmOverloads
    fun map(value: PaymentInfo, statement: String? = null): PaymentResultMethod.Model = value.run {
        val amountModel = paymentResultAmountMapper.map(this)

        val builder = if (details?.isNotEmpty() == true) {
            PaymentResultMethod.Model.Builder(amountModel, details)
        } else {
            val details = listOfNotNull(
                getDescription(this)?.let { getDetailPaymentCongratsText(it) },
                description,
                getStatement(this, statement)?.let { getDetailPaymentCongratsText(it) }
            )
            PaymentResultMethod.Model.Builder(amountModel, details)
        }

        when {
            extraInfo?.isNotEmpty() == true -> extraInfo
            consumerCreditsInfo != null -> listOfNotNull(
                consumerCreditsInfo.title.takeUnless { it.isNullOrBlank() }?.let {
                    PaymentCongratsText(it, textColor = "#CC000000", fontSize = 16f)
                },
                consumerCreditsInfo.subtitle.takeUnless { it.isNullOrBlank() }?.let {
                    PaymentCongratsText(it, textColor = "#73000000", fontSize = 14f)
                }
            )
            else -> null
        }?.let { builder.setExtraInfo(it) }

        iconUrl?.let { builder.setImageUrl(it) }
        return builder.build()
    }

    private fun getDetailPaymentCongratsText(description: String) = PaymentCongratsText(description, textColor = "#999999", fontSize = 14f)

    private fun getDescription(paymentInfo: PaymentInfo) = paymentInfo.run {
        val paymentTypeId = paymentMethodType.value
        if (PaymentTypes.isCardPaymentType(paymentTypeId)) {
            String.format(
                Locale.getDefault(), "%s %s %s",
                paymentMethodName,
                context.resources.getString(R.string.px_ending_in),
                lastFourDigits
            )
        } else if (!PaymentTypes.isAccountMoney(paymentTypeId) || description == null || description.message.isBlank()) {
            paymentMethodName
        } else {
            null
        }
    }

    private fun getStatement(paymentInfo: PaymentInfo, statement: String?) = paymentInfo.run {
        val paymentTypeId = paymentMethodType.value
        if (PaymentTypes.isCardPaymentType(paymentTypeId) && statement.isNotNullNorEmpty()) {
            TextUtil.format(context, R.string.px_text_state_account_activity_congrats, statement)
        } else {
            null
        }
    }
}
