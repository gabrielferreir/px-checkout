package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.MPTracker

class SecurityCodeTracker(
    private val tracker: MPTracker,
    private val securityCodeViewTrack: SecurityCodeViewTrack,
    private val confirmSecurityCodeTrack: ConfirmSecurityCodeTrack,
    private val abortSecurityCodeTrack: AbortSecurityCodeTrack,
    private val securityCodeFrictions: SecurityCodeFrictions) {

    fun getViewTrackPath() = securityCodeViewTrack.getTrack().path

    fun trackConfirmSecurityCode() {
        tracker.track(confirmSecurityCodeTrack)
    }

    fun trackSecurityCode() {
        tracker.track(securityCodeViewTrack)
    }

    fun trackAbortSecurityCode() {
        tracker.track(abortSecurityCodeTrack)
    }

    fun trackPaymentApiError() {
        tracker.track(securityCodeFrictions.paymentApiErrorFriction())
    }

    fun trackTokenApiError() {
        tracker.track(securityCodeFrictions.tokenApiErrorFriction())
    }
}
