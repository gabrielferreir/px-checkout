package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.TargetBehaviourTrackData

class TargetBehaviourEvent(viewTrack: TrackWrapper, data: TargetBehaviourTrackData) : TrackWrapper() {

    private val track = TrackFactory.withEvent(getPath(viewTrack)).addData(data.toMap()).build()

    private fun getPath(viewTracker: TrackWrapper) = "${viewTracker.getTrack()?.path}/target_behaviour"

    override fun getTrack() = track
}