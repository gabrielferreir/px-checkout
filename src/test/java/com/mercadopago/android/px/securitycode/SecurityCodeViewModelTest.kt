package com.mercadopago.android.px.securitycode

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mercadopago.android.px.core.internal.FlowConfigurationProvider
import com.mercadopago.android.px.internal.base.use_case.CallBack
import com.mercadopago.android.px.internal.base.use_case.TokenizeWithCvvUseCase
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton
import com.mercadopago.android.px.internal.features.security_code.SecurityCodeViewModel
import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessSecurityCodeDisplayData
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.DisplayDataUseCase
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.SecurityTrackModelUseCase
import com.mercadopago.android.px.internal.features.security_code.mapper.TrackingParamModelMapper
import com.mercadopago.android.px.internal.features.security_code.model.SecurityCodeDisplayModel
import com.mercadopago.android.px.internal.features.security_code.tracking.SecurityCodeTracker
import com.mercadopago.android.px.internal.mappers.CardUiMapper
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.Token
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.tracking.internal.model.Reason
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class SecurityCodeViewModelTest {

    @Mock
    private lateinit var tokenizeUseCaseTest: TokenizeWithCvvUseCase

    @Mock
    private lateinit var trackModelUseCase: SecurityTrackModelUseCase

    @Mock
    private lateinit var displayDataUseCaseTest: DisplayDataUseCase

    @Mock
    private lateinit var trackingParamModelMapper: TrackingParamModelMapper

    @Mock
    private lateinit var paymentConfiguration: PaymentConfiguration

    @Mock
    private lateinit var cardUiMapper: CardUiMapper

    @Mock
    private lateinit var card: Card

    @Mock
    private lateinit var paymentRecovery: PaymentRecovery

    @Mock
    private lateinit var displayModelObserver: Observer<SecurityCodeDisplayModel>

    @Mock
    private lateinit var tokenizeErrorApiObserver: Observer<Unit>
    @Mock
    private lateinit var flowConfigurationProvider: FlowConfigurationProvider
    private lateinit var securityCodeViewModel: SecurityCodeViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        securityCodeViewModel = SecurityCodeViewModel(
            tokenizeUseCaseTest,
            displayDataUseCaseTest,
            trackModelUseCase,
            trackingParamModelMapper,
            cardUiMapper,
            flowConfigurationProvider,
            mock()
        )

        securityCodeViewModel.displayModelLiveData.observeForever(displayModelObserver)
        securityCodeViewModel.tokenizeErrorApiLiveData.observeForever(tokenizeErrorApiObserver)

        whenever(trackingParamModelMapper.map(any(), any())).thenReturn(mock())
        with(card) {
            whenever(id).thenReturn("123")
            whenever(getSecurityCodeLength()).thenReturn(3)
            whenever(getSecurityCodeLocation()).thenReturn("front")
        }
    }

    @Test
    fun whenInitSecurityCodeViewModelWithCard() {
        val successTrackerCaptor = argumentCaptor<CallBack<SecurityCodeTracker>>()
        val successDisplayDataCaptor = argumentCaptor<CallBack<BusinessSecurityCodeDisplayData>>()
        val securityCodeTrackerMock = mock<SecurityCodeTracker>()
        val displayBusinessDataMock = mock<BusinessSecurityCodeDisplayData> {
            on { title }.thenReturn(mock())
            on { message }.thenReturn(mock())
        }

        securityCodeViewModel.init(
            paymentConfiguration,
            card,
            null,
            Reason.INVALID_ESC
        )

        verify(trackModelUseCase).execute(any(), successTrackerCaptor.capture(), any())
        successTrackerCaptor.firstValue.invoke(securityCodeTrackerMock)
        verify(securityCodeTrackerMock).trackSecurityCode()
        verify(displayDataUseCaseTest).execute(any(), successDisplayDataCaptor.capture(), any())
        successDisplayDataCaptor.firstValue.invoke(displayBusinessDataMock)
        verify(displayModelObserver).onChanged(any())
        verifyNoInteractions(tokenizeUseCaseTest)
    }

    @Test
    fun whenInitSecurityCodeViewModelWithPaymentRecovery() {
        val successTrackerCaptor = argumentCaptor<CallBack<SecurityCodeTracker>>()
        val successDisplayDataCaptor = argumentCaptor<CallBack<BusinessSecurityCodeDisplayData>>()
        val securityCodeTrackerMock = mock<SecurityCodeTracker>()
        val displayBusinessDataMock = mock<BusinessSecurityCodeDisplayData> {
            on { title }.thenReturn(mock())
            on { message }.thenReturn(mock())
        }

        whenever(paymentRecovery.card).thenReturn(card)

        securityCodeViewModel.init(
            paymentConfiguration,
            null,
            paymentRecovery,
            Reason.INVALID_ESC
        )

        verify(trackModelUseCase).execute(any(), successTrackerCaptor.capture(), any())
        successTrackerCaptor.firstValue.invoke(securityCodeTrackerMock)
        verify(securityCodeTrackerMock).trackSecurityCode()
        verify(displayDataUseCaseTest).execute(any(), successDisplayDataCaptor.capture(), any())
        successDisplayDataCaptor.firstValue.invoke(displayBusinessDataMock)
        verify(displayModelObserver).onChanged(any())
        verifyNoInteractions(tokenizeUseCaseTest)
    }

    @Test
    fun whenSecurityCodeViewModelTokenizeAndSuccess() {
        val callbackMock = mock<ConfirmButton.OnEnqueueResolvedCallback>()
        val successTokenCaptor = argumentCaptor<CallBack<Token>>()
        val tokenMock = mock<Token>()
        securityCodeViewModel.init(
            paymentConfiguration,
            card,
            paymentRecovery,
            Reason.INVALID_ESC
        )
        securityCodeViewModel.enqueueOnExploding("123", callbackMock)

        verify(tokenizeUseCaseTest).execute(any(), successTokenCaptor.capture(), any())
        successTokenCaptor.firstValue.invoke(tokenMock)
        verify(callbackMock).success()
    }

    @Test
    fun whenSecurityCodeViewModelTokenizeAndFail() {
        val successTrackerCaptor = argumentCaptor<CallBack<SecurityCodeTracker>>()
        val callbackMock = mock<ConfirmButton.OnEnqueueResolvedCallback>()
        val failureTokenCaptor = argumentCaptor<CallBack<MercadoPagoError>>()
        val securityCodeTrackerMock = mock<SecurityCodeTracker>()
        val errorMock = mock<MercadoPagoError>()

        securityCodeViewModel.init(
            paymentConfiguration,
            card,
            paymentRecovery,
            Reason.INVALID_ESC
        )

        verify(trackModelUseCase).execute(any(), successTrackerCaptor.capture(), any())
        successTrackerCaptor.firstValue.invoke(securityCodeTrackerMock)
        securityCodeViewModel.enqueueOnExploding("123", callbackMock)

        verify(tokenizeUseCaseTest).execute(any(), any(), failureTokenCaptor.capture())
        failureTokenCaptor.firstValue.invoke(errorMock)
        verify(tokenizeErrorApiObserver).onChanged(any())
        verify(callbackMock).failure(any())
    }

    @Test
    fun whenSecurityCodeViewModelAndHandlePrepayment() {
        val successTrackerCaptor = argumentCaptor<CallBack<SecurityCodeTracker>>()
        val securityCodeTrackerMock = mock<SecurityCodeTracker>()
        val callback = mock<ConfirmButton.OnReadyForProcessCallback>()

        securityCodeViewModel.init(
            paymentConfiguration,
            card,
            paymentRecovery,
            Reason.INVALID_ESC
        )

        verify(trackModelUseCase).execute(any(), successTrackerCaptor.capture(), any())
        successTrackerCaptor.firstValue.invoke(securityCodeTrackerMock)

        securityCodeViewModel.handlePrepayment(callback)

        verify(securityCodeTrackerMock).trackConfirmSecurityCode()
        verify(callback).call(paymentConfiguration)
    }

    @Test
    fun whenSecurityCodeViewModelOnBack() {
        val successTrackerCaptor = argumentCaptor<CallBack<SecurityCodeTracker>>()
        val securityCodeTrackerMock = mock<SecurityCodeTracker>()

        securityCodeViewModel.init(
            paymentConfiguration,
            card,
            paymentRecovery,
            Reason.INVALID_ESC
        )

        verify(trackModelUseCase).execute(any(), successTrackerCaptor.capture(), any())
        successTrackerCaptor.firstValue.invoke(securityCodeTrackerMock)

        securityCodeViewModel.onBack()

        verify(securityCodeTrackerMock).trackAbortSecurityCode()
    }

    @Test
    fun whenSecurityCodeViewModelOnPaymentError() {
        val successTrackerCaptor = argumentCaptor<CallBack<SecurityCodeTracker>>()
        val securityCodeTrackerMock = mock<SecurityCodeTracker>()

        securityCodeViewModel.init(
            paymentConfiguration,
            card,
            paymentRecovery,
            Reason.INVALID_ESC
        )

        verify(trackModelUseCase).execute(any(), successTrackerCaptor.capture(), any())
        successTrackerCaptor.firstValue.invoke(securityCodeTrackerMock)

        securityCodeViewModel.onPaymentError()

        verify(securityCodeTrackerMock).trackPaymentApiError()
    }
}
