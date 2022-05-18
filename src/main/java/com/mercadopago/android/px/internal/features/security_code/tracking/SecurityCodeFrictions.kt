package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel
import java.util.*

class SecurityCodeFrictions(model: TrackingMapModel) {
    private val data = model.toMap()

    fun paymentApiErrorFriction(): FrictionEventTracker {
        val frictionId = FrictionEventTracker.Id.PAYMENTS_API_ERROR

        return FrictionEventTracker.with(
            "${SecurityCodeTrack.ACTION_BASE_PATH}/${frictionId.name.toLowerCase(Locale.getDefault())}",
            frictionId,
            FrictionEventTracker.Style.SNACKBAR,
            data)
    }

    fun tokenApiErrorFriction():FrictionEventTracker {
        val frictionId = FrictionEventTracker.Id.TOKEN_API_ERROR

        return FrictionEventTracker.with(
            "${SecurityCodeTrack.ACTION_BASE_PATH}/${frictionId.name.toLowerCase(Locale.getDefault())}",
            frictionId,
            FrictionEventTracker.Style.SNACKBAR,
            data)
    }
}