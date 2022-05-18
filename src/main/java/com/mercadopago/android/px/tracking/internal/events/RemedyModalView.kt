package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

internal class RemedyModalView : TrackWrapper() {

    override fun getTrack() = TrackFactory.withView(PATH).build()

    companion object {
        private const val PATH = "$BASE_PATH/result/error/remedy/modal"
    }
}