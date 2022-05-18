package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

class ConfirmSecurityCodeTrack(
    securityCodeData: TrackingMapModel,
    reason: Reason,
    override val actionPath: String = "/confirm") : SecurityCodeTrack(securityCodeData, reason)