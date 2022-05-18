package com.mercadopago.android.px.internal.util

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.addons.model.EscDeleteReason
import com.mercadopago.android.px.internal.callbacks.awaitTaggedCallback
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.ifSuccess
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.Token
import com.mercadopago.android.px.model.PaymentMethod
import com.mercadopago.android.px.model.SavedESCCardToken
import com.mercadopago.android.px.model.CardToken
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.exceptions.CardTokenException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.model.Reason

internal class TokenCreationWrapper private constructor(builder: Builder) {

    private val cardTokenRepository = builder.cardTokenRepository
    private val escManagerBehaviour = builder.escManagerBehaviour
    private val card = builder.card
    private val token = builder.token
    private val paymentMethod = builder.paymentMethod
    private val reason = builder.reason

    suspend fun createTokenWithEsc(esc: String): Response<Token, MercadoPagoError> {
        val cardId = if (card != null) card.id!! else token!!.cardId
        val body = SavedESCCardToken.createWithEsc(cardId, esc)

        return createESCToken(body)
    }

    suspend fun createTokenWithCvv(cvv: String): Response<Token, MercadoPagoError> {
        val body = SavedESCCardToken.createWithSecurityCode(card!!.id.orEmpty(), cvv)
            .also { it.validateSecurityCode(card) }

        return createESCToken(body)
    }

    suspend fun createTokenWithoutCvv(): Response<Token, MercadoPagoError> {
        val body = SavedESCCardToken.createWithoutSecurityCode(card!!.id.orEmpty())

        return createESCToken(body)
    }

    suspend fun cloneToken(cvv: String) = when (val response = doCloneToken()) {
        is Response.Success -> putCVV(cvv, response.result.id)
        is Response.Failure -> response
    }

    @Throws(CardTokenException::class)
    fun validateCVVFromToken(cvv: String): Boolean {
        if (token?.firstSixDigits.isNotNullNorEmpty()) {
            CardToken.validateSecurityCode(cvv, paymentMethod, token!!.firstSixDigits)
        } else if (!CardToken.validateSecurityCode(cvv)) {
            throw CardTokenException(CardTokenException.INVALID_FIELD)
        }
        return true
    }

    private suspend fun createESCToken(savedESCCardToken: SavedESCCardToken) = cardTokenRepository
        .createToken(savedESCCardToken)
        .awaitTaggedCallback(ApiUtil.RequestOrigin.CREATE_TOKEN).ifSuccess {
            if (Reason.ESC_CAP == reason) { // Remove previous esc for tracking purpose
                escManagerBehaviour.deleteESCWith(savedESCCardToken.cardId, EscDeleteReason.ESC_CAP, null)
            }
            cardTokenRepository.clearCap(savedESCCardToken.cardId) {}
        }.ifSuccess { token -> if (card != null) token.lastFourDigits = card.lastFourDigits }

    private suspend fun doCloneToken() = cardTokenRepository
        .cloneToken(token!!.id)
        .awaitTaggedCallback(ApiUtil.RequestOrigin.CREATE_TOKEN)

    private suspend fun putCVV(cvv: String, tokenId: String) = cardTokenRepository
        .putSecurityCode(cvv, tokenId)
        .awaitTaggedCallback(ApiUtil.RequestOrigin.CREATE_TOKEN)

    class Builder(val cardTokenRepository: CardTokenRepository, val escManagerBehaviour: ESCManagerBehaviour) {
        var card: Card? = null
            private set

        var token: Token? = null
            private set

        var paymentMethod: PaymentMethod? = null
            private set

        var reason: Reason? = Reason.NO_REASON
            private set

        fun with(card: Card) = apply {
            this.card = card
            this.paymentMethod = card.paymentMethod
        }

        fun with(token: Token) = apply { this.token = token }
        fun with(paymentMethod: PaymentMethod) = apply { this.paymentMethod = paymentMethod }
        fun with(paymentRecovery: PaymentRecovery) = apply {
            card = paymentRecovery.card
            token = paymentRecovery.token
            paymentMethod = paymentRecovery.paymentMethod
            reason = Reason.from(paymentRecovery)
        }

        fun build(): TokenCreationWrapper {
            check(!(token == null && card == null)) { "Token and card can't both be null" }

            checkNotNull(paymentMethod) { "Payment method not set" }

            return TokenCreationWrapper(this)
        }
    }
}