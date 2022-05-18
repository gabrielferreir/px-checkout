package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.DiscountItemData

class DiscountItemEvent(viewTrack: TrackWrapper, index: Int, trackId: String?) : TrackWrapper() {

    private val eventPath = viewTrack.getTrack()?.path + "/tap_discount_item"
    private val data = DiscountItemData(index, trackId)

    override fun getTrack() = TrackFactory.withEvent(eventPath).addData(data.toMap()).build()
}