package com.mercadopago.android.px.internal.features.security_code.mapper

import com.mercadopago.android.px.internal.features.security_code.domain.use_case.SecurityTrackModelUseCase.*
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.tracking.internal.model.Reason

class TrackingParamModelMapper {

    fun map(card: Card, reason: Reason): SecurityTrackModelParams {

        val cardTrackParams = CardTrackParams(
            card.id.orEmpty(),
            card.paymentMethod!!.id,
            card.paymentMethod!!.paymentTypeId,
            card.issuer!!.id,
            card.firstSixDigits.orEmpty()
        )

        return SecurityTrackModelParams(cardTrackParams, reason)
    }
}