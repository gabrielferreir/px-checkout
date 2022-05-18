package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.addons.model.Track
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.PaymentMethod
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.AvailableMethod
import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.views.CvvAskViewTracker

class CVVRecoveryFrictionTrack private constructor(card: Card, paymentMethod: PaymentMethod, reason: Reason): TrackWrapper() {

    private val frictionTracker: FrictionEventTracker

    init {
        val path = CvvAskViewTracker(card, paymentMethod.paymentTypeId, reason).getTrack().path

        frictionTracker = FrictionEventTracker.with(path, FrictionEventTracker.Id.INVALID_CVV,
            FrictionEventTracker.Style.CUSTOM_COMPONENT, AvailableMethod.from(paymentMethod).toMap())
    }

    override fun getTrack(): Track {
        return frictionTracker.getTrack()
    }

    companion object {
        @JvmStatic fun with(card: Card?, reason: Reason): CVVRecoveryFrictionTrack? {
            return card?.paymentMethod?.let {
                CVVRecoveryFrictionTrack(card, it, reason)
            }
        }
    }
}
