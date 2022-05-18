package com.mercadopago.android.px.internal.helpers

import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo
import com.mercadopago.android.px.internal.features.payment_result.model.DisplayInfoHelper
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.CustomSearchItem
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.PaymentMethod
import com.mercadopago.android.px.model.display_info.CustomSearchItemDisplayInfo
import com.mercadopago.android.px.model.display_info.CustomSearchItemDisplayInfo.Result.ExtraInfo
import com.mercadopago.android.px.model.display_info.DisplayInfo
import com.mercadopago.android.px.model.display_info.ResultInfo
import com.mercadopago.android.px.model.internal.Text
import com.mercadopago.android.px.model.internal.TextAlignment
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

private const val CUSTOM_OPTION_ID = "123"
private const val DESCRIPTION_TITLE = "Debito inmediato"
private const val BANK_NAME = "Banco Ciudad"
private const val CBU = "CBU: ***4412"
private const val ICON_URL = "http://ddd.png"
private const val RESULT_INFO_TITLE = "result_info_title"
private const val RESULT_INFO_SUBTITLE = "result_info_subtitle"
private const val BACKGROUND_COLOR = "#000000"
private const val TEXT_COLOR = "#FFFFFF"
private const val WEIGHT = "semi_bold"

class OfflinePaymentMethodDisplayInfoHelperTest {

    @MockK
    private lateinit var payerPaymentMethodRepository: PayerPaymentMethodRepository

    @MockK
    private lateinit var userSelectionRepository: UserSelectionRepository

    @MockK
    private lateinit var paymentData: PaymentData

    @MockK
    private lateinit var payerPaymentMethod: CustomSearchItem

    private lateinit var displayInfoHelper: DisplayInfoHelper

    @Before
    fun `set up`() {
        MockKAnnotations.init(this)
        displayInfoHelper = DisplayInfoHelper(payerPaymentMethodRepository, userSelectionRepository)
    }

    @Test
    fun `given payment method info comes from payerPaymentMethod then paymentInfo texts are taken from payerPaymentMethod display info`() {
        val paymentInfoBuilder = PaymentInfo.Builder()
        val title = Text(DESCRIPTION_TITLE, BACKGROUND_COLOR, TEXT_COLOR, WEIGHT, null)
        val bankName = Text(BANK_NAME, BACKGROUND_COLOR, TEXT_COLOR, WEIGHT, null)
        val cbu = Text(CBU, BACKGROUND_COLOR, TEXT_COLOR, WEIGHT, null)

        val customSearchItemDisplayInfoResultPaymentMethod = mockkClass(CustomSearchItemDisplayInfo.Result.PaymentMethod::class) {
            every { detail } returns listOf(title, bankName, cbu)
            every { iconUrl } returns ICON_URL
        }
        val customSearchItemDisplayInfoResult = mockkClass(CustomSearchItemDisplayInfo.Result::class) {
            every { paymentMethod } returns customSearchItemDisplayInfoResultPaymentMethod
            every { extraInfo } returns null
        }
        val customSearchItemDisplayInfo = mockkClass(CustomSearchItemDisplayInfo::class) {
            every { result } returns customSearchItemDisplayInfoResult
        }

        every { payerPaymentMethod.displayInfo } returns customSearchItemDisplayInfo
        every { userSelectionRepository.customOptionId } returns CUSTOM_OPTION_ID
        every { payerPaymentMethodRepository[CUSTOM_OPTION_ID] } returns payerPaymentMethod

        displayInfoHelper.resolve(paymentData, paymentInfoBuilder)

        val paymentInfo = paymentInfoBuilder.build()

        assertNotNull(paymentInfo.details)
        paymentInfo.details!!.size.assertEquals(3)

        with(paymentInfo.details!![0]) {
            assertEquals(DESCRIPTION_TITLE, message)
            assertEquals(BACKGROUND_COLOR, backgroundColor)
            assertEquals(TEXT_COLOR, textColor)
            assertEquals(WEIGHT, weight)
            assertEquals(TextAlignment.LEFT, alignment)
        }

        with(paymentInfo.details!![1]) {
            assertEquals(BANK_NAME, message)
            assertEquals(BACKGROUND_COLOR, backgroundColor)
            assertEquals(TEXT_COLOR, textColor)
            assertEquals(WEIGHT, weight)
            assertEquals(TextAlignment.LEFT, alignment)
        }

        with(paymentInfo.details!![2]) {
            assertEquals(CBU, message)
            assertEquals(BACKGROUND_COLOR, backgroundColor)
            assertEquals(TEXT_COLOR, textColor)
            assertEquals(WEIGHT, weight)
            assertEquals(TextAlignment.LEFT, alignment)
        }
    }

    @Test
    fun `given payment method info comes from paymentData then paymentInfo texts are taken from paymentData display info`() {
        val paymentInfoBuilder = PaymentInfo.Builder()
        setupPaymentMethodFromPaymentData(null)

        displayInfoHelper.resolve(paymentData, paymentInfoBuilder)

        val paymentInfo = paymentInfoBuilder.build()

        assertEquals(RESULT_INFO_TITLE, paymentInfo.consumerCreditsInfo!!.title)
        assertEquals(RESULT_INFO_SUBTITLE, paymentInfo.consumerCreditsInfo!!.subtitle)

        with(paymentInfo.description!!) {
            assertEquals(DESCRIPTION_TITLE, message)
            assertEquals(BACKGROUND_COLOR, backgroundColor)
            assertEquals(TEXT_COLOR, textColor)
            assertEquals(WEIGHT, weight)
            assertEquals(TextAlignment.LEFT, alignment)
        }
    }

    @Test
    fun `given payer payment method contains extra info then paymentInfo should contain this extra info`() {
        val paymentInfoBuilder = PaymentInfo.Builder()
        val firstExtraInfoText = Text("1234", BACKGROUND_COLOR, TEXT_COLOR, WEIGHT, TextAlignment.CENTER)
        val secondExtraInfoText = Text("12345", BACKGROUND_COLOR, TEXT_COLOR, WEIGHT, TextAlignment.LEFT)
        setupPaymentMethodFromPaymentData(ExtraInfo(listOf(firstExtraInfoText, secondExtraInfoText)))

        displayInfoHelper.resolve(paymentData, paymentInfoBuilder)
        val paymentInfo = paymentInfoBuilder.build()
        assertNotNull(paymentInfo.extraInfo)
        with(paymentInfo.extraInfo!!) {
            size.assertEquals(2)
            assertPaymentCongratsText(get(0), firstExtraInfoText)
            assertPaymentCongratsText(get(1), secondExtraInfoText)
        }
    }

    private fun assertPaymentCongratsText(actual: PaymentCongratsText, expected: Text) {
        with(actual) {
            message.assertEquals(expected.message)
            backgroundColor!!.assertEquals(expected.backgroundColor!!)
            textColor!!.assertEquals(expected.textColor!!)
            weight!!.assertEquals(expected.weight!!)
            alignment.assertEquals(expected.alignment!!)
        }
    }

    private fun setupPaymentMethodFromPaymentData(extraInfo: ExtraInfo?) {
        val resultInfoMock = mockkClass(ResultInfo::class) {
            every { title } returns RESULT_INFO_TITLE
            every { subtitle } returns RESULT_INFO_SUBTITLE
        }
        val displayInfoMock = mockkClass(DisplayInfo::class) {
            every { description } returns Text(DESCRIPTION_TITLE, BACKGROUND_COLOR, TEXT_COLOR, WEIGHT, null)
            every { resultInfo } returns resultInfoMock
        }
        val paymentMethodMock = mockkClass(PaymentMethod::class) {
            every { displayInfo } returns displayInfoMock
        }

        every { paymentData.paymentMethod } returns paymentMethodMock
        every { userSelectionRepository.customOptionId } returns CUSTOM_OPTION_ID
        every { payerPaymentMethodRepository[CUSTOM_OPTION_ID] } returns payerPaymentMethod
        val displayInfo = extraInfo?.let {
            mockkClass(CustomSearchItemDisplayInfo::class) {
                every { result } returns mockkClass(CustomSearchItemDisplayInfo.Result::class)
            }
        }?.also {
            every { it.result.paymentMethod } returns null
            every { it.result.extraInfo } returns extraInfo
        }
        every { payerPaymentMethod.displayInfo } returns displayInfo
    }
}
