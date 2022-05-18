package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class DownloadAppEvent(viewTrack: TrackWrapper) : TrackWrapper() {

    private val eventPath = viewTrack.getTrack()?.path + "/tap_download_app"

    override fun getTrack() = TrackFactory.withEvent(eventPath).build()
}