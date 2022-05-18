package com.mercadopago.android.px.internal.usecases

import com.mercadopago.android.px.TestContextProvider
import com.mercadopago.android.px.internal.base.use_case.CallBack
import com.mercadopago.android.px.internal.features.validation_program.AuthenticateUseCase
import com.mercadopago.android.px.internal.features.validation_program.ValidationProgramUseCase
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.model.*
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import com.mercadopago.android.px.tracking.internal.events.ProgramValidationEvent
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner::class)
class ValidationProgramUseCaseTest {

    @Mock
    private lateinit var success: CallBack<String?>

    @Mock
    private lateinit var failure: CallBack<MercadoPagoError>

    @Mock
    private lateinit var applicationSelectionRepository: ApplicationSelectionRepository

    @Mock
    private lateinit var authenticateUseCase: AuthenticateUseCase

    @Mock
    private lateinit var tracker: MPTracker

    private lateinit var validationProgramUseCase: ValidationProgramUseCase

    @Before
    fun setUp() {
        validationProgramUseCase = ValidationProgramUseCase(
            applicationSelectionRepository, authenticateUseCase, tracker, TestContextProvider())
    }

    @Test
    fun whenPaymentDataListIsNull() {
        validationProgramUseCase.execute(
            null,
            success::invoke,
            failure::invoke
        )

        verifyNoInteractions(success)
        verify(tracker).track(any<FrictionEventTracker>())
        verify(failure).invoke(any())
    }

    @Test
    fun whenPaymentDataListIsEmpty() {
        validationProgramUseCase.execute(
            listOf(),
            success::invoke,
            failure::invoke
        )

        verifyNoInteractions(success)
        verify(tracker).track(any<FrictionEventTracker>())
        verify(failure).invoke(any())
    }

    @Test
    fun whenIsNotKnownValidationProgram() {
        whenever(applicationSelectionRepository[any<String>()]).thenReturn(mock())

        validationProgramUseCase.execute(
            listOf(createPaymentData()),
            success::invoke,
            failure::invoke
        )

        verify(success).invoke(null)
        verify(tracker).track(any<ProgramValidationEvent>())
        verifyNoInteractions(failure)
    }

    @Test
    fun whenIsSTPValidationProgram() {
        val validationProgram: Application.ValidationProgram = mock {
            on { id }.thenReturn("STP")
        }
        val application: Application = mock {
            on { validationPrograms }.thenReturn(listOf(validationProgram))
        }
        whenever(applicationSelectionRepository[any<String>()]).thenReturn(application)

        validationProgramUseCase.execute(
            listOf(createPaymentData()),
            success::invoke,
            failure::invoke
        )

        runBlocking {
            verify(authenticateUseCase).execute(any())
            verify(success).invoke("stp")
            verify(tracker).track(any())
            verifyNoInteractions(failure)
        }
    }

    private fun createPaymentData(): PaymentData {
        val discount = mock<Discount>()
        val paymentMethod = mock<PaymentMethod> {
            on { id }.thenReturn("visa")
        }

        val token = mock<Token>()
        return PaymentData.Builder()
            .setToken(token)
            .setDiscount(discount)
            .setRawAmount(BigDecimal.TEN)
            .setPaymentMethod(paymentMethod)
            .createPaymentData()
    }
}
