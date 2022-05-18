package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class ScoreEvent(viewTrack: TrackWrapper) : TrackWrapper() {

    private val eventPath = viewTrack.getTrack()?.path + "/tap_score"

    override fun getTrack() = TrackFactory.withEvent(eventPath).build()
}