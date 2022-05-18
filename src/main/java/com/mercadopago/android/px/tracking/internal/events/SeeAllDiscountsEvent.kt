package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class SeeAllDiscountsEvent(viewTrack: TrackWrapper) : TrackWrapper() {

    private val eventPath = viewTrack.getTrack()?.path + "/tap_see_all_discounts"

    override fun getTrack() = TrackFactory.withEvent(eventPath).build()
}