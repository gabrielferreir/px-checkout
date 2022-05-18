package com.mercadopago.android.px.internal.viewmodel

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.R
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.view.MPTextView
import com.mercadopago.android.px.model.AmountConfiguration
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.model.InterestFree
import com.mercadopago.android.px.model.PayerCost
import com.mercadopago.android.px.utils.PayerCostUtils
import com.mercadopago.android.px.utils.TextUtils
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreditCardDescriptorModelTest : BasicRobolectricTest() {

    private lateinit var currency: Currency
    private lateinit var amountConfiguration: AmountConfiguration
    private lateinit var interestFree: InterestFree
    private var textView: MPTextView = MPTextView(getContext())
    private var spannableStringBuilder = SpannableStringBuilder()

    @Before
    fun setUp() {
        currency = Mockito.mock(Currency::class.java)
        amountConfiguration = Mockito.mock(AmountConfiguration::class.java)
        interestFree = Mockito.mock(InterestFree::class.java)
        whenever(currency.symbol).thenReturn("$")
    }

    @Test
    fun updateLeftSpannableWithoutInterestShouldSetTextWithInstallmentsAndInterestFreeMessage() {
        val payerCost = PayerCostUtils.getPayerCost(3, 0, 40)
        whenever(amountConfiguration.splitConfiguration).thenReturn(null)
        whenever(amountConfiguration.getCurrentPayerCost(any(), any())).thenReturn(payerCost)
        whenever(interestFree.installmentRow).thenReturn(TextUtils.getText("Interest free"))
        whenever(interestFree.hasAppliedInstallment(any())).thenReturn(true)
        val model =
            CreditCardDescriptorModel.createFrom(currency, null, interestFree, amountConfiguration)
        model.updateLeftSpannable(spannableStringBuilder, textView)
        spannableStringBuilder.toString()
            .assertEquals("${payerCost.installments}x $${payerCost.installmentAmount}")
    }

    @Test
    fun updateLeftSpannableWithoutInterestShouldSetPrimaryColorForInstallmentsAndInstallmentRowColorForInterestFree() {
        val payerCost = PayerCostUtils.getPayerCost(3, 0, 40)
        val installmentsText = "${payerCost.installments}x $${payerCost.installmentAmount}"
        whenever(amountConfiguration.splitConfiguration).thenReturn(null)
        whenever(amountConfiguration.getCurrentPayerCost(any(), any())).thenReturn(payerCost)
        whenever(interestFree.installmentRow).thenReturn(TextUtils.getText("Interest free", color = "#123456"))
        whenever(interestFree.hasAppliedInstallment(any())).thenReturn(true)
        val model =
            CreditCardDescriptorModel.createFrom(currency, null, interestFree, amountConfiguration)
        model.updateLeftSpannable(spannableStringBuilder, textView)
        val installmentsTextColor =
            ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.px_expressCheckoutTextColor))
        spannableStringBuilder.getSpans(0, installmentsText.length, ForegroundColorSpan::class.java)
            .first().foregroundColor.assertEquals(installmentsTextColor.foregroundColor)
    }

    @Test
    fun updateLeftSpannableWithInterestShouldSetSecondaryTextColorForTotalAmountAndInterestRateMessage() {
        val payerCost = PayerCostUtils.getPayerCost(3, 10, 44, 132, 10)
        whenever(amountConfiguration.splitConfiguration).thenReturn(null)
        whenever(amountConfiguration.getCurrentPayerCost(any(), any())).thenReturn(payerCost)
        val model =
            CreditCardDescriptorModel.createFrom(currency, null, null, amountConfiguration)
        model.updateLeftSpannable(spannableStringBuilder, textView)
        val installmentsText = "${payerCost.installments}x $${payerCost.installmentAmount}"
        val totalText = "($${payerCost.totalAmount})"
        val interestRateText = payerCost.interestRate.message
        spannableStringBuilder.toString().assertEquals("$installmentsText $totalText $interestRateText")
    }

    @Test
    fun updateLeftSpannableWithInterestShouldSetTextWithInstallmentsAndTotalAmountAndInterestRateMessageColor() {
        val payerCost = PayerCostUtils.getPayerCost(3, 10, 44, 132, 10)
        whenever(amountConfiguration.splitConfiguration).thenReturn(null)
        whenever(amountConfiguration.getCurrentPayerCost(any(), any())).thenReturn(payerCost)
        val model =
            CreditCardDescriptorModel.createFrom(currency, null, null, amountConfiguration)
        model.updateLeftSpannable(spannableStringBuilder, textView)
        val installmentsText = "${payerCost.installments}x $${payerCost.installmentAmount}"
        val totalText = "($${payerCost.totalAmount})"

        val totalAmountColor =
            ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.px_checkout_secondary_color))
        spannableStringBuilder.getSpans(
            installmentsText.length,
            installmentsText.length + totalText.length,
            ForegroundColorSpan::class.java
        ).first().foregroundColor.assertEquals(totalAmountColor.foregroundColor)

        val interestRateColor = ForegroundColorSpan(Color.parseColor(payerCost.interestRate.textColor))
        spannableStringBuilder.getSpans(
            installmentsText.length + totalText.length + 1,
            spannableStringBuilder.length,
            ForegroundColorSpan::class.java
        ).first().foregroundColor.assertEquals(interestRateColor.foregroundColor)
    }

    @Test
    fun updateRightSpannableWithPayerCostSelectedShouldBeEmptyText() {
        val model =
            CreditCardDescriptorModel.createFrom(currency, null, null, amountConfiguration)
        model.setCurrentPayerCost(0)
        model.updateRightSpannable(spannableStringBuilder, textView)
        assertTrue(spannableStringBuilder.isEmpty())
    }

    @Test
    fun updateRightSpannableWithNoPayerCostSelectedShouldSetInstallmentsRightHeaderText() {
        val model = CreditCardDescriptorModel.createFrom(
            currency,
            TextUtils.getText("Right spannable", color = "#999999"),
            null,
            amountConfiguration
        )
        model.setCurrentPayerCost(PayerCost.NO_SELECTED)
        model.updateRightSpannable(spannableStringBuilder, textView)
        spannableStringBuilder.toString().assertEquals("Right spannable")
        val color = ForegroundColorSpan(Color.parseColor("#999999"))
        spannableStringBuilder.getSpans(0, spannableStringBuilder.length, ForegroundColorSpan::class.java)
            .first().foregroundColor.assertEquals(color.foregroundColor)
    }
}