package com.mercadopago.android.px.internal.base.use_case

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.callbacks.mapError
import com.mercadopago.android.px.internal.extensions.ifFailure
import com.mercadopago.android.px.internal.extensions.ifSuccess
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.internal.util.TokenCreationWrapper
import com.mercadopago.android.px.internal.util.TokenErrorWrapper
import com.mercadopago.android.px.model.Token
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker

private typealias Cvv = String

internal class TokenizeWithCvvUseCase(
    private val cardTokenRepository: CardTokenRepository,
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val userSelectionRepository: UserSelectionRepository,
    private val paymentSettingRepository: PaymentSettingRepository,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<Cvv, Token>(tracker) {

    override suspend fun doExecute(param: Cvv): Response<Token, MercadoPagoError> {
        val card = userSelectionRepository.card
        checkNotNull(card) { "Card selected should not be null" }
        return TokenCreationWrapper
            .Builder(cardTokenRepository, escManagerBehaviour)
            .with(card)
            .with(card.paymentMethod!!)
            .build()
            .createTokenWithCvv(param)
            .ifSuccess {
                escManagerBehaviour.saveESCWith(it.cardId, it.esc)
                paymentSettingRepository.configure(it)
            }
            .mapError { error ->
                MercadoPagoError.createRecoverable(TokenErrorWrapper(error.apiException).value)
            }
            .ifFailure {
                paymentSettingRepository.clearToken()
            }
    }
}
