package com.mercadopago.android.px.internal.viewmodel

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.R
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.view.MPTextView
import com.mercadopago.android.px.model.AmountConfiguration
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.utils.PayerCostUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DebitCardDescriptorModelTest : BasicRobolectricTest() {
    private lateinit var currency: Currency
    private lateinit var amountConfiguration: AmountConfiguration
    private var textView: MPTextView = MPTextView(getContext())
    private var spannableStringBuilder = SpannableStringBuilder()

    @Before
    fun setUp() {
        currency = Mockito.mock(Currency::class.java)
        amountConfiguration = Mockito.mock(AmountConfiguration::class.java)
        whenever(currency.symbol).thenReturn("$")
    }

    @Test
    fun updateLeftSpannableShouldSetTextWithInstallmentAmountAndPrimaryTextColor() {
        val payerCost = PayerCostUtils.getPayerCost(1, 0, 120)
        whenever(amountConfiguration.splitConfiguration).thenReturn(null)
        whenever(amountConfiguration.getCurrentPayerCost(any(), any())).thenReturn(payerCost)
        val model =
            DebitCardDescriptorModel.createFrom(currency, amountConfiguration)
        model.updateLeftSpannable(spannableStringBuilder, textView)
        spannableStringBuilder.toString().assertEquals("$ ${payerCost.totalAmount}")
        val color = ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.px_expressCheckoutTextColor))
        spannableStringBuilder.getSpans(0, spannableStringBuilder.length, ForegroundColorSpan::class.java)
            .first().foregroundColor.assertEquals(color.foregroundColor)
    }
}