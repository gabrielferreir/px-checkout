package com.mercadopago.android.px.internal.base.use_case

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.callbacks.mapError
import com.mercadopago.android.px.internal.extensions.ifFailure
import com.mercadopago.android.px.internal.extensions.ifSuccess
import com.mercadopago.android.px.internal.model.EscStatus
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.internal.repository.EscPaymentManager
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.internal.util.TokenCreationWrapper
import com.mercadopago.android.px.internal.util.TokenErrorWrapper
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.Token
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.exceptions.SecurityCodeRequiredError
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.EscFrictionEventTracker
import com.mercadopago.android.px.tracking.internal.events.TokenFrictionEventTracker.Companion.create
import com.mercadopago.android.px.tracking.internal.model.Reason

internal class TokenizeWithEscUseCase(
    private val cardTokenRepository: CardTokenRepository,
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val escPaymentManager: EscPaymentManager,
    private val paymentSettingRepository: PaymentSettingRepository,
    private val userSelectionRepository: UserSelectionRepository,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<Unit, Token>(tracker) {

    override suspend fun doExecute(param: Unit) = run {

        if (PaymentTypes.isCardPaymentType(userSelectionRepository.paymentMethod?.paymentTypeId)) {
            val card = userSelectionRepository.card
            if (card != null && hasPayerCost()) {
                tokenizeCard(card)
            } else if (hasValidNewCardInfo()) {
                Response.Success(Token())
            } else {
                Response.Failure(MercadoPagoError("Something went wrong", false))
            }
        } else {
            Response.Success(Token())
        }
    }

    private suspend fun tokenizeCard(card: Card): Response<Token, MercadoPagoError> {
        val tokenCreationWrapper = TokenCreationWrapper
            .Builder(cardTokenRepository, escManagerBehaviour)
            .with(card)
            .with(card.paymentMethod!!)
            .build()

        return if (shouldTokenizeWithCvv(card)) {
            if (canTokenizeCardWithEsc(card)) {
                val esc = escManagerBehaviour.getESC(card.id, card.firstSixDigits, card.lastFourDigits)
                tokenCreationWrapper.createTokenWithEsc(esc!!)
                    .mapError { error ->
                        SecurityCodeRequiredError(TokenErrorWrapper(error.apiException).toReason(), card)
                    }
                    .ifFailure {
                        val tokenErrorWrapper = TokenErrorWrapper(it.apiException)
                        if (tokenErrorWrapper.isKnownTokenError) {
                            // Just limit the tracking to esc api exception
                            tracker.track(EscFrictionEventTracker.create(card.id.orEmpty(), esc, it.apiException))
                        } else {
                            tracker.track(create(tokenErrorWrapper.value))
                        }
                        escManagerBehaviour.deleteESCWith(
                            card.id.orEmpty(),
                            tokenErrorWrapper.toEscDeleteReason(),
                            tokenErrorWrapper.value
                        )
                    }
            } else {
                val reason = if (escManagerBehaviour.isESCEnabled) {
                    if (shouldInvalidateEsc(card.escStatus)) Reason.ESC_CAP else Reason.SAVED_CARD
                } else Reason.ESC_DISABLED

                Response.Failure(SecurityCodeRequiredError(reason, card))
            }
        } else {
            tokenCreationWrapper.createTokenWithoutCvv()
        }.ifSuccess {
            escManagerBehaviour.saveESCWith(it.cardId, it.esc)
            paymentSettingRepository.configure(it)
        }.ifFailure {
            paymentSettingRepository.clearToken()
        }
    }

    private fun hasValidNewCardInfo() = with(userSelectionRepository) {
        paymentMethod != null && issuer != null && payerCost != null && paymentSettingRepository.hasToken()
    }

    private fun shouldTokenizeWithCvv(card: Card): Boolean {
        return card.isSecurityCodeRequired()
    }

    private fun canTokenizeCardWithEsc(card: Card) = escManagerBehaviour.isESCEnabled &&
        escPaymentManager.hasEsc(card) &&
        !shouldInvalidateEsc(card.escStatus)

    private fun hasPayerCost() = userSelectionRepository.payerCost != null

    private fun shouldInvalidateEsc(escStatus: String?): Boolean {
        return EscStatus.REJECTED == escStatus
    }
}
