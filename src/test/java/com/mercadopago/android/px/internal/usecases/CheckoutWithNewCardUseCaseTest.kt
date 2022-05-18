package com.mercadopago.android.px.internal.usecases

import com.mercadopago.android.px.CallbackTest
import com.mercadopago.android.px.TestContextProvider
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.datasource.CheckoutRepositoryImpl
import com.mercadopago.android.px.internal.domain.CheckoutWithNewCardUseCase
import com.mercadopago.android.px.internal.util.ApiUtil
import com.mercadopago.android.px.mocks.CheckoutResponseStub
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.tracking.internal.MPTracker
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CheckoutWithNewCardUseCaseTest {

    @Mock
    private lateinit var success: CallbackTest<CheckoutResponse>

    @Mock
    private lateinit var failure: CallbackTest<MercadoPagoError>

    @Mock
    private lateinit var tracker: MPTracker

    @Mock
    private lateinit var checkoutRepository: CheckoutRepositoryImpl

    private lateinit var testContextProvider: TestContextProvider
    private lateinit var checkoutWithNewCardUseCase: CheckoutWithNewCardUseCase

    @Before
    fun setUp() {
        testContextProvider = TestContextProvider()
        checkoutWithNewCardUseCase = CheckoutWithNewCardUseCase(checkoutRepository, tracker, testContextProvider)
    }

    @Test
    fun whenApiReturnsSuccessAndCardIdProvidedThenItShouldCallSortAndConfigure() {
        val checkoutResponse = CheckoutResponseStub.ONE_TAP_VISA_CREDIT_CARD.get()
        val oneTapItem = checkoutResponse.oneTapItems.first { it.isCard }
        runBlocking {
            whenever(checkoutRepository.checkoutWithNewCard(any())).thenReturn(Response.Success(checkoutResponse))
        }
        checkoutWithNewCardUseCase.execute(
            oneTapItem.card.id,
            success::invoke,
            failure::invoke
        )
        runBlocking {
            verify(checkoutRepository).checkoutWithNewCard(oneTapItem.card.id)
            verify(checkoutRepository).configure(checkoutResponse)
        }
    }

    @Test
    fun whenApiResponseDoesNotHaveCardThenItShouldReturnRecoverableMPError() {
        runBlocking {
            // This is needed because if we don't use the same context on runBlocking and on UseCase
            // then when we use delay the test fails.
            val newCheckoutWithNewCardUseCase = CheckoutWithNewCardUseCase(
                checkoutRepository,
                tracker,
                TestContextProvider(coroutineContext, coroutineContext)
            )
            whenever(checkoutRepository.checkoutWithNewCard("123")).thenReturn(
                Response.Failure(
                    MercadoPagoError(
                        ApiException().apply { message = "Card not found" },
                        ApiUtil.RequestOrigin.POST_INIT
                    )
                )
            )
            newCheckoutWithNewCardUseCase.execute(
                "123",
                success::invoke,
                failure::invoke
            )
        }
        verify(failure).invoke(
            argThat { this.apiException.message.contains("Card not found") && this.isRecoverable }
        )
    }
}
