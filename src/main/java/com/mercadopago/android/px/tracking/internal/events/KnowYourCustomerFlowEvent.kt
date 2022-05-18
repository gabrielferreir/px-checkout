package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class KnowYourCustomerFlowEvent(viewTrack: TrackWrapper) : TrackWrapper() {
    private val track = TrackFactory.withEvent(getPath(viewTrack)).build()

    private fun getPath(viewTracker: TrackWrapper) = viewTracker.getTrack()?.path + "/start_kyc_flow"

    override fun getTrack() = track
}