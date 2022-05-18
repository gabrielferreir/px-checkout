package com.mercadopago.android.px.internal.features.security_code.domain.use_case

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.features.security_code.tracking.*
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

class SecurityTrackModelUseCase(
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<SecurityTrackModelUseCase.SecurityTrackModelParams, SecurityCodeTracker>(tracker) {

    override suspend fun doExecute(param: SecurityTrackModelParams) = param.card.let { card ->
        val model = SecurityTrackModel(card)
        val securityCodeTracker = SecurityCodeTracker(
            tracker,
            SecurityCodeViewTrack(model, param.reason),
            ConfirmSecurityCodeTrack(model, param.reason),
            AbortSecurityCodeTrack(model, param.reason),
            SecurityCodeFrictions(model)
        )

        Response.Success(securityCodeTracker)
    }

    data class SecurityTrackModelParams(val card: CardTrackParams, val reason: Reason)
    data class CardTrackParams(
        val cardId: String,
        val paymentMethodId: String,
        val paymentTypeId: String,
        val issuerId: Long,
        val firstSixDigits: String
    )

    internal class SecurityTrackModel(private val card: CardTrackParams) : TrackingMapModel() {
        override fun toMap() = mapOf(
            "payment_method_id" to card.paymentMethodId,
            "payment_method_type" to card.paymentTypeId,
            "card_id" to card.cardId,
            "issuer_id" to card.issuerId,
            "bin" to card.firstSixDigits
        )
    }
}
