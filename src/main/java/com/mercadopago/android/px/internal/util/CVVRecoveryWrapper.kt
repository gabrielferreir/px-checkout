package com.mercadopago.android.px.internal.util

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.Token
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.CVVRecoveryFrictionTrack
import com.mercadopago.android.px.tracking.internal.model.Reason

internal class CVVRecoveryWrapper(
    cardTokenRepository: CardTokenRepository,
    escManagerBehaviour: ESCManagerBehaviour,
    private val paymentRecovery: PaymentRecovery,
    private val tracker: MPTracker
) {

    private val tokenCreationWrapper: TokenCreationWrapper = TokenCreationWrapper
        .Builder(cardTokenRepository, escManagerBehaviour)
        .with(paymentRecovery)
        .build()
    private val card = paymentRecovery.card
    private val token = paymentRecovery.token

    suspend fun recoverWithCVV(cvv: String): Response<Token, MercadoPagoError> {
        var response: Response<Token, MercadoPagoError> = Response.Failure(MercadoPagoError.createNotRecoverable(""))

        if (hasToCloneToken() && tokenCreationWrapper.validateCVVFromToken(cvv)) {
            response = tokenCreationWrapper.cloneToken(cvv)
        } else if (hasToRecoverTokenFromESC()) {
            response = tokenCreationWrapper.createTokenWithCvv(cvv)
        }

        response.resolve(error = {
            CVVRecoveryFrictionTrack.with(card, Reason.from(paymentRecovery))?.let {
                tracker.track(it)
            }
        })

        return response
    }

    private fun hasToCloneToken() = token?.run { cardId.isNullOrEmpty() } ?: false

    private fun hasToRecoverTokenFromESC() = paymentRecovery.isStatusDetailInvalidESC &&
        (token?.cardId.isNotNullNorEmpty() || card?.id.isNotNullNorEmpty())
}
