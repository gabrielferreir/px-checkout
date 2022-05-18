package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class ViewReceiptEvent(viewTrack: TrackWrapper) : TrackWrapper() {

    private val eventPath = viewTrack.getTrack()?.path + "/tap_view_receipt"

    override fun getTrack() = TrackFactory.withEvent(eventPath).build()
}