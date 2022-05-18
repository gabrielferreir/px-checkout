package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class CrossSellingEvent(viewTrack: TrackWrapper) : TrackWrapper() {

    private val eventPath: String = viewTrack.getTrack()?.path + "/tap_cross_selling"

    override fun getTrack() = TrackFactory.withEvent(eventPath).build()
}