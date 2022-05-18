package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

internal class RemedyModalAbortEvent : TrackWrapper() {

    override fun getTrack() = TrackFactory.withEvent(PATH).build()

    companion object {
        private const val PATH = "$BASE_PATH/result/error/remedy/modal/abort"
    }
}