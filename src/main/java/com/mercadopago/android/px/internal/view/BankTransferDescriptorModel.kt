package com.mercadopago.android.px.internal.view

import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.util.textformatter.AmountLabeledFormatter

internal class BankTransferDescriptorModel private constructor(private val sliderTitle: String) :
    PaymentMethodDescriptorView.Model() {

    override fun updateLeftSpannable(
        spannableStringBuilder: SpannableStringBuilder,
        textView: TextView
    ) {
        val context = textView.context

        val amountLabeledFormatter = AmountLabeledFormatter(spannableStringBuilder, context)
            .withTextColor(ContextCompat.getColor(context, R.color.px_expressCheckoutTextColor))
        amountLabeledFormatter.apply(sliderTitle)
    }

    companion object {
        fun createFrom(sliderTitle: String) : BankTransferDescriptorModel {
            return BankTransferDescriptorModel(sliderTitle)
        }
    }
}