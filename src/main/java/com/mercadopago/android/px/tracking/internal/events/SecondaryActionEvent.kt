package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class SecondaryActionEvent(viewTracker: TrackWrapper) : TrackWrapper() {

    private val eventPath = viewTracker.getTrack()?.path + ACTION_PATH

    override fun getTrack() = TrackFactory.withEvent(eventPath).build()

    companion object {
        private const val ACTION_PATH = "/secondary_action"
    }
}