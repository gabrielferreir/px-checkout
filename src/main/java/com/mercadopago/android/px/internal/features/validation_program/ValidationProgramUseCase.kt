package com.mercadopago.android.px.internal.features.validation_program

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.Application.KnownValidationProgram
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.ProgramValidationEvent

internal class ValidationProgramUseCase(
    private val applicationSelectionRepository: ApplicationSelectionRepository,
    private val authenticateUseCase: AuthenticateUseCase,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<List<PaymentData>?, String?>(tracker) {

    override suspend fun doExecute(param: List<PaymentData>?): Response<String?, MercadoPagoError> {
        val mainPaymentData = param?.firstOrNull() ?: throw IllegalStateException("No payment data available")
        val payerPaymentMethodId = mainPaymentData.token?.cardId ?: mainPaymentData.paymentMethod.id
        val validationProgram = applicationSelectionRepository[payerPaymentMethodId].validationPrograms?.firstOrNull()
        val knownValidationProgram = KnownValidationProgram[validationProgram?.id]
        when (knownValidationProgram) {
            KnownValidationProgram.STP -> authenticateUseCase.execute(mainPaymentData)
        }
        val validationProgramId = knownValidationProgram?.value
        tracker.track(ProgramValidationEvent(validationProgramId))
        return Response.Success(validationProgramId)
    }
}