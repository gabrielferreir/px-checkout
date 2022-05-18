package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel
import java.util.Locale

abstract class SecurityCodeTrack(securityCodeData: TrackingMapModel, reason: Reason) : TrackWrapper() {

    protected val data = mutableMapOf<String, Any>(
        "reason" to reason.name.toLowerCase(Locale.getDefault())
    ).also { it.putAll(securityCodeData.toMap()) }
    abstract val actionPath: String

    override fun getTrack() = TrackFactory.withView("$ACTION_BASE_PATH$actionPath").addData(data).build()

    companion object {
        const val ACTION_BASE_PATH = "${BASE_PATH}/security_code"
    }
}