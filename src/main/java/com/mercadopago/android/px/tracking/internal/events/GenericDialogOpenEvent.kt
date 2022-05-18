package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.GenericDialogTrackData

class GenericDialogOpenEvent(data: GenericDialogTrackData.Open) : TrackWrapper() {

    private val track = TrackFactory.withEvent("${BASE_PATH}/dialog/open")
        .addData(data.toMap()).build()

    override fun getTrack() = track
}