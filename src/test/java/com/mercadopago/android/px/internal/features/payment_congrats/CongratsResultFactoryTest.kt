package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModelMapper
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
internal class CongratsResultFactoryTest {

    private lateinit var congratsResultFactory: CongratsResultFactory

    @Mock
    private lateinit var paymentCongratsModelMapper: PaymentCongratsModelMapper

    @Mock
    private lateinit var paymentCongratsModel: PaymentCongratsModel

    private val redirectUrl = "redirect_url"

    @Before
    fun setUp() {
        whenever(paymentCongratsModelMapper.map(any<BusinessPaymentModel>())).thenReturn(paymentCongratsModel)

        congratsResultFactory = CongratsResultFactory(paymentCongratsModelMapper)
    }

    @Test
    fun `On redirect url is not empty then return skip congrats result`() {

        val paymentModel = mock<PaymentModel>()
        val expectedCongratsResult = CongratsPaymentResult.SkipCongratsResult(paymentModel)
        val congratsResult = congratsResultFactory.create(paymentModel, redirectUrl)

        congratsResult.assertEquals(expectedCongratsResult)
    }

    @Test
    fun `When payment model is business and redirect url is null then return business paymentResult`() {

        val businessPaymentModel = mock<BusinessPaymentModel>()

        val expectedCongratsResult = CongratsResult.BusinessPaymentResult(paymentCongratsModel)
        val congratsResult = congratsResultFactory.create(businessPaymentModel, null)

        congratsResult.assertEquals(expectedCongratsResult)
    }

    @Test
    fun `On default payment model and redirect url is null then payment result`() {
        val paymentModel = mock<PaymentModel>()

        val expectedCongratsResult = CongratsResult.PaymentResult(paymentModel)
        val congratsResult = congratsResultFactory.create(paymentModel, null)

        congratsResult.assertEquals(expectedCongratsResult)
    }
}
