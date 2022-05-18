package com.mercadopago.android.px.tracking.internal

import com.mercadopago.android.px.addons.model.Track
import com.mercadopago.android.px.addons.tracking.Tracker

object TrackFactory {

    private const val APPLICATION_CONTEXT = "px"

    @JvmStatic
    fun withView(path: String) =
        Track.Builder(Tracker.MELIDATA, APPLICATION_CONTEXT, Track.Type.VIEW, path)
            .addTrackers(listOf(Tracker.GOOGLE_ANALYTICS, Tracker.CUSTOM))

    @JvmStatic
    fun withEvent(path: String) =
        Track.Builder(Tracker.MELIDATA, APPLICATION_CONTEXT, Track.Type.EVENT, path)
            .addTrackers(listOf(Tracker.GOOGLE_ANALYTICS, Tracker.CUSTOM))
}