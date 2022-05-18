package com.mercadopago.android.px.internal.view

import android.view.Gravity
import android.widget.TextView
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.assertChildCount
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.assertGone
import com.mercadopago.android.px.assertText
import com.mercadopago.android.px.assertVisible
import com.mercadopago.android.px.getField
import com.mercadopago.android.px.internal.di.Session
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentResultInfo
import com.mercadopago.android.px.internal.mappers.PaymentResultAmountMapper
import com.mercadopago.android.px.internal.mappers.PaymentResultMethodMapper
import com.mercadopago.android.px.internal.util.CurrenciesUtil
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.internal.util.PaymentDataHelper
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.model.Discount
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.PaymentMethod
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.Token
import com.mercadopago.android.px.model.display_info.DisplayInfo
import com.mercadopago.android.px.model.internal.Text
import com.mercadopago.android.px.model.internal.TextAlignment
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import java.math.BigDecimal
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val BACKGROUND_COLOR = "#000000"
private const val TEXT_COLOR = "#C3000000"
private const val WEIGHT = "semi_bold"

@RunWith(RobolectricTestRunner::class)
class PaymentResultMethodTest : BasicRobolectricTest() {

    private lateinit var methodView: PaymentResultMethod

    @RelaxedMockK
    private lateinit var currency: Currency

    @MockK
    private lateinit var discount: Discount

    @MockK
    private lateinit var token: Token

    @MockK
    private lateinit var paymentMethod: PaymentMethod

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        methodView = PaymentResultMethod(getContext())
        Session.initialize(getContext())
        with(currency) {
            every { decimalPlaces } returns 2
            every { decimalSeparator } returns ','
            every { thousandsSeparator } returns '.'
        }
        with(discount) {
            every { name } returns "discount name"
            every { couponAmount } returns BigDecimal.ONE
        }
    }

    @Test
    fun whenInitWithCreditCardThenViewsAreCorrectlyLabeled() {
        val paymentMethodName = "Mastercard"
        val paymentMethodStatement = "pm_statement"
        val lastFourDigits = "2222"
        val infoTitle = "infoTitle"
        val infoSubtitle = "infoSubtitle"
        val paymentInfo = setupPaymentInfo(infoTitle, infoSubtitle, paymentMethodStatement, paymentMethodName, lastFourDigits)
        methodView.setModel(PaymentResultMethodMapper(getContext(), PaymentResultAmountMapper).map(paymentInfo.build()))
        with(methodView) {
            getField<AdapterLinearLayout>("details").apply {
                assertVisible()
                assertChildCount<MPTextView>(2)
                (getChildAt(0) as MPTextView).assertText("$paymentMethodName completed in $lastFourDigits")
                (getChildAt(1) as MPTextView).assertText(paymentMethodStatement)
            }
            getField<AdapterLinearLayout>("extraInfo").apply {
                assertVisible()
                assertChildCount<MPTextView>(2)
                (getChildAt(0) as MPTextView).assertText(infoTitle)
                (getChildAt(1) as MPTextView).assertText(infoSubtitle)
            }
        }
    }

    @Test
    fun givenThereIsExtraInfoWhenInitThenExtraInfoIsVisibleAndCorrectlyAligned() {
        val paymentInfo = setupPaymentInfo("1234", "test", "paymentMethodStatement",
            "Dinero en cuenta", "1234")
        val firstExtraInfoText = PaymentCongratsText.from(Text("1234", BACKGROUND_COLOR, TEXT_COLOR, WEIGHT, TextAlignment.CENTER))!!
        // To test default gravity
        val secondExtraInfoText = PaymentCongratsText.from(Text("12345", null, null, null, null))!!
        paymentInfo.withExtraInfo(listOf(firstExtraInfoText, secondExtraInfoText))
        methodView.setModel(PaymentResultMethodMapper(getContext(), PaymentResultAmountMapper).map(paymentInfo.build(), null))
        with(methodView) {
            with(getField<AdapterLinearLayout>("extraInfo")){
                assertVisible()
                assertChildCount<MPTextView>(2)
                with(getChildAt(0) as MPTextView) {
                    assertText("1234")
                    assertHorizontalAlignment(gravity, Gravity.CENTER_HORIZONTAL)
                }
                with(getChildAt(1) as MPTextView) {
                    assertText("12345")
                    assertHorizontalAlignment(gravity, Gravity.START)
                }
            }
        }
    }

    private fun assertHorizontalAlignment(actualGravity: Int, expectedGravity: Int) {
        (actualGravity and Gravity.HORIZONTAL_GRAVITY_MASK).assertEquals(expectedGravity and Gravity.HORIZONTAL_GRAVITY_MASK)
    }

    private fun setupPaymentInfo(
        infoTitle: String,
        infoSubtitle: String,
        paymentMethodStatement: String,
        paymentMethodName: String,
        lastFourDigits: String
    ): PaymentInfo.Builder {
        val displayInfo = JsonUtil.fromJson(
            """{
                "result_info": {
                    "title": "$infoTitle",
                    "subtitle": "$infoSubtitle"
                },
                "description": {
                    "message": "$paymentMethodStatement",
                    "text_color": "#ffffff",
                    "background_color": "#000000",
                    "weight": "regular"
                }
            }""".trimIndent(), DisplayInfo::class.java
        )
        every { paymentMethod.paymentTypeId } returns PaymentTypes.CREDIT_CARD
        every { paymentMethod.name } returns paymentMethodName
        every { paymentMethod.displayInfo } returns displayInfo!!
        every { token.lastFourDigits } returns lastFourDigits
        val paymentData = PaymentData.Builder()
            .setToken(token)
            .setDiscount(discount)
            .setRawAmount(BigDecimal.TEN)
            .setNoDiscountAmount(BigDecimal.TEN)
            .setPaymentMethod(paymentMethod)
            .createPaymentData()
        val paymentResultInfo = displayInfo.resultInfo?.let {
            PaymentResultInfo(it.title, it.subtitle)
        }
        val paymentCongratsText = PaymentCongratsText.from(displayInfo.description!!)

        return PaymentInfo.Builder()
            .withPaymentMethodName(paymentData.paymentMethod.name)
            .withDescription(paymentCongratsText)
            .withPaymentMethodType(PaymentInfo.PaymentMethodType.fromName(paymentData.paymentMethod.paymentTypeId))
            .withConsumerCreditsInfo(paymentResultInfo)
            .withPaidAmount(getPrettyAmount(currency, PaymentDataHelper.getPrettyAmountToPay(paymentData)))
            .withDiscountData(paymentData.discount!!.name, getPrettyAmount(currency, paymentData.noDiscountAmount))
            .withInstallmentsData(BigDecimal.TEN.toInt(), null, null, null)
            .withLastFourDigits(paymentData.token!!.lastFourDigits)
    }

    private fun getPrettyAmount(currency: Currency, amount: BigDecimal): String? {
        return CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(currency, amount)
    }
}
