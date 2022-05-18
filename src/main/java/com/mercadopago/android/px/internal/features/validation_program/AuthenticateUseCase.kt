package com.mercadopago.android.px.internal.features.validation_program

import com.mercadopago.android.px.addons.ThreeDSBehaviour
import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.repository.CardHolderAuthenticatorRepository
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker

internal class AuthenticateUseCase @JvmOverloads constructor(
    tracker: MPTracker,
    private val threeDSBehaviour: ThreeDSBehaviour,
    private val cardHolderAuthenticatorRepository: CardHolderAuthenticatorRepository,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<PaymentData, Any>(tracker) {

    override suspend fun doExecute(param: PaymentData): Response<Any, MercadoPagoError> {
        val response = cardHolderAuthenticatorRepository.authenticate(
            param,
            threeDSBehaviour.getAuthenticationParameters() ?: error("Missing authentication params")
        )

        return Response.Success(response)
    }
}