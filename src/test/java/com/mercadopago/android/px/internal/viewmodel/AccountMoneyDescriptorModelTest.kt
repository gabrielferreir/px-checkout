package com.mercadopago.android.px.internal.viewmodel

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.R
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.view.MPTextView
import com.mercadopago.android.px.model.AccountMoneyMetadata
import com.mercadopago.android.px.model.Currency
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
class AccountMoneyDescriptorModelTest : BasicRobolectricTest() {

    private lateinit var currency: Currency
    private var textView: MPTextView = MPTextView(getContext())
    private var spannableStringBuilder = SpannableStringBuilder()

    @Before
    fun setUp() {
        currency = mock(Currency::class.java)
    }

    @Test
    fun updateLeftSpannableWithoutDisplayInfoShouldSetEmptyText() {
        val accountMoneyMetadata = getAccountMoneyMetadataWithoutDisplayInfo()
        val model = AccountMoneyDescriptorModel.createFrom(accountMoneyMetadata, currency, BigDecimal.TEN)
        model.updateLeftSpannable(spannableStringBuilder, textView)
        assertTrue(spannableStringBuilder.isBlank())
    }

    @Test
    fun updateLeftSpannableWithSliderTitleInDisplayInfoShouldContainSliderTitleAsText() {
        val accountMoneyMetadata = getAccountMoneyMetadata("Slider title")
        val model = AccountMoneyDescriptorModel.createFrom(accountMoneyMetadata, currency, BigDecimal.TEN)
        model.updateLeftSpannable(spannableStringBuilder, textView)
        val color = ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.px_expressCheckoutTextColor))
        spannableStringBuilder.getSpans(0, textView.text.length, ForegroundColorSpan::class.java).forEach {
            it.foregroundColor.assertEquals(color.foregroundColor)
        }
        spannableStringBuilder.toString().assertEquals("Slider title")
    }

    @Test
    fun updateLeftSpannableWithEmptySliderTitleInDisplayInfoShouldSetEmptyText() {
        val accountMoneyMetadata = getAccountMoneyMetadata(TextUtil.EMPTY)
        val model = AccountMoneyDescriptorModel.createFrom(accountMoneyMetadata, currency, BigDecimal.TEN)
        model.updateLeftSpannable(spannableStringBuilder, textView)
        assertTrue(spannableStringBuilder.isBlank())
    }

    private fun getAccountMoneyMetadataWithoutDisplayInfo() : AccountMoneyMetadata {
        return JsonUtil.fromJson(
            """{
            "available_balance": 16725.22,
            "invested": true,
            "display_info": null
        }""".trimIndent(), AccountMoneyMetadata::class.java)!!
    }

    private fun getAccountMoneyMetadata(sliderTitle: String) : AccountMoneyMetadata {
        return JsonUtil.fromJson(
            """{
            "available_balance": 16725.22,
            "invested": true,
            "display_info": {
                "type": "default",
                "slider_title": "$sliderTitle",
                "message": "Balance in Mercado Pago: ${'$'} 16.725,22"
            }
        }""".trimIndent(), AccountMoneyMetadata::class.java)!!
    }
}