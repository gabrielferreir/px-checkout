package com.mercadopago.android.px.tracking.internal

import com.mercadopago.android.px.addons.model.Track
import com.mercadopago.android.px.addons.tracking.Tracker

object DiscountCenterTrackFactory {

    private const val APPLICATION_CONTEXT = "px"

    @JvmStatic
    fun withEvent(path: String) =
        Track.Builder(Tracker.MELIDATA, APPLICATION_CONTEXT, Track.Type.EVENT, path)
            .addTracker(Tracker.CUSTOM)
}
