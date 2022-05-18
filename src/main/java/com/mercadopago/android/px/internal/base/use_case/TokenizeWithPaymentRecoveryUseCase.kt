package com.mercadopago.android.px.internal.base.use_case

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.ifSuccess
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.util.CVVRecoveryWrapper
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.Token
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker

internal class TokenizeWithPaymentRecoveryUseCase(
    private val cardTokenRepository: CardTokenRepository,
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val paymentSettingRepository: PaymentSettingRepository,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<TokenizeWithPaymentRecoveryParams, Token>(tracker) {
    override suspend fun doExecute(param: TokenizeWithPaymentRecoveryParams): Response<Token, MercadoPagoError> {
        return CVVRecoveryWrapper(cardTokenRepository, escManagerBehaviour, param.paymentRecovery, tracker)
            .recoverWithCVV(param.securityCode).ifSuccess { paymentSettingRepository.configure(it) }
    }
}

internal data class TokenizeWithPaymentRecoveryParams(val paymentRecovery: PaymentRecovery, val securityCode: String)
