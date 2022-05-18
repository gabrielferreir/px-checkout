package com.mercadopago.android.px.internal.usecases

import com.mercadopago.android.px.CallbackTest
import com.mercadopago.android.px.TestContextProvider
import com.mercadopago.android.px.addons.ThreeDSBehaviour
import com.mercadopago.android.px.addons.model.ThreeDSDataOnlyParams
import com.mercadopago.android.px.internal.features.validation_program.AuthenticateUseCase
import com.mercadopago.android.px.internal.repository.CardHolderAuthenticatorRepository
import com.mercadopago.android.px.model.Discount
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.PaymentMethod
import com.mercadopago.android.px.model.Token
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner::class)
class AuthenticateUseCaseTest {

    @Mock
    private lateinit var success: CallbackTest<Any>

    @Mock
    private lateinit var failure: CallbackTest<MercadoPagoError>

    @Mock
    private lateinit var tracker: MPTracker

    @Mock
    private lateinit var threeDSBehaviour: ThreeDSBehaviour

    @Mock
    private lateinit var cardHolderAuthenticatorRepository: CardHolderAuthenticatorRepository

    private lateinit var authenticateUseCase: AuthenticateUseCase

    @Before
    fun setUp() {
        authenticateUseCase = AuthenticateUseCase(
            tracker, threeDSBehaviour, cardHolderAuthenticatorRepository, TestContextProvider())
    }

    @Test
    fun whenAuthenticationParametersIsNull() {
        authenticateUseCase.execute(
            createPaymentData(),
            success::invoke,
            failure::invoke
        )

        verifyNoInteractions(success)
        verify(failure).invoke(any())
    }

    @Test
    fun whenAuthenticationParametersIsValid() = runBlocking {
        val paymentData = createPaymentData()
        val authenticationParameters: ThreeDSDataOnlyParams = mock()
        whenever(threeDSBehaviour.getAuthenticationParameters()).thenReturn(authenticationParameters)
        whenever(cardHolderAuthenticatorRepository.authenticate(any(), any())).thenReturn(mock())
        authenticateUseCase.execute(
            paymentData,
            success::invoke,
            failure::invoke
        )

        verifyNoInteractions(failure)
        verify(cardHolderAuthenticatorRepository).authenticate(paymentData, authenticationParameters)
        verify(success).invoke(any())
    }

    private fun createPaymentData(): PaymentData {
        val discount = mock<Discount>()
        val paymentMethod = mock<PaymentMethod>()

        val token = mock<Token>()
        return PaymentData.Builder()
            .setToken(token)
            .setDiscount(discount)
            .setRawAmount(BigDecimal.TEN)
            .setPaymentMethod(paymentMethod)
            .createPaymentData()
    }
}
