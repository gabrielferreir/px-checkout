package com.mercadopago.android.px.internal.features.payment_result

import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.internal.features.security_code.RenderModeMapper
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.PaymentMethod
import com.mercadopago.android.px.model.PaymentResult
import com.mercadopago.android.px.tracking.internal.BankInfoHelper
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

private const val PAYMENT_METHOD_ID = "123456"

@RunWith(MockitoJUnitRunner::class)
class PaymentResultPresenterTest {

    @Mock
    private lateinit var tracker: MPTracker
    @Mock
    private lateinit var renderModeMapper: RenderModeMapper

    private lateinit var presenter: PaymentResultPresenter

    @Before
    fun setUp() {
        val paymentsSettings = mock<PaymentSettingRepository>()
        val advancedConfiguration = mock<AdvancedConfiguration>()
        val paymentModel = mock<PaymentModel>()
        val paymentResult = mock<PaymentResult>()
        val paymentData = mock<PaymentData>()
        val paymentMethod  = mock<PaymentMethod>()
        val bankInfoHelper = mock<BankInfoHelper>()

        whenever(paymentMethod.id).thenReturn(PAYMENT_METHOD_ID)
        whenever(paymentData.paymentMethod).thenReturn(paymentMethod)
        whenever(paymentModel.paymentResult).thenReturn(paymentResult)
        whenever(paymentModel.congratsResponse).thenReturn(mock())
        whenever(paymentModel.remedies).thenReturn(mock())
        whenever(paymentResult.paymentData).thenReturn(paymentData)
        whenever(paymentsSettings.checkoutPreference).thenReturn(mock())
        whenever(paymentsSettings.currency).thenReturn(mock())
        whenever(paymentsSettings.advancedConfiguration).thenReturn(advancedConfiguration)
        whenever(advancedConfiguration.paymentResultScreenConfiguration).thenReturn(mock())

        presenter = PaymentResultPresenter(
            paymentsSettings,
            paymentModel,
            mock(),
            true,
            mock(),
            mock(),
            renderModeMapper,
            bankInfoHelper,
            tracker
        )
    }

    @Test
    fun whenOnFreshStartThenTrackView() {
        presenter.onFreshStart()

        verify(tracker).track(any<ResultViewTrack>())
        verifyNoMoreInteractions(tracker)
    }

    @Test
    fun whenOnDirtyStartThenNotTrackView() {
        verifyNoMoreInteractions(tracker)
    }
}
