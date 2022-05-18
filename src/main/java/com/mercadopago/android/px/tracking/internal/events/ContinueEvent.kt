package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class ContinueEvent(viewTracker: TrackWrapper) : TrackWrapper() {

    private val eventPath = viewTracker.getTrack()?.path + "/continue"

    override fun getTrack() = TrackFactory.withEvent(eventPath).build()
}