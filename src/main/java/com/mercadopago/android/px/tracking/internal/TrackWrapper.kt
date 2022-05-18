package com.mercadopago.android.px.tracking.internal

import com.mercadopago.android.px.addons.model.Track

abstract class TrackWrapper {

    abstract fun getTrack(): Track?
    open val shouldTrackExperimentsLabel = true

    companion object {
        const val BASE_PATH = "/px_checkout"
        const val PAYMENTS_PATH = "/payments"
    }
}
