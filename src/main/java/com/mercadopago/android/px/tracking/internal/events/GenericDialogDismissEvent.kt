package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.GenericDialogTrackData

class GenericDialogDismissEvent(data: GenericDialogTrackData.Dismiss) : TrackWrapper() {

    private val track = TrackFactory.withEvent("${BASE_PATH}/dialog/dismiss")
        .addData(data.toMap()).build()

    override fun getTrack() = track
}