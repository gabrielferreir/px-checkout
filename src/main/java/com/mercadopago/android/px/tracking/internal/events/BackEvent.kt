package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class BackEvent(viewTrack: TrackWrapper) : TrackWrapper() {

    private val eventPath: String = viewTrack.getTrack()?.path + PATH

    override fun getTrack() = TrackFactory.withEvent(eventPath).build()

    companion object {
        private const val PATH = "/back"
    }
}