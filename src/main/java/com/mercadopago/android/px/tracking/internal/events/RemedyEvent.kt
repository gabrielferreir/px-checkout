package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.RemedyTrackData

internal class RemedyEvent(private val data: RemedyTrackData, showedModal: Boolean): TrackWrapper() {
    private val event = if(showedModal) MODAL else VIEW
    private val body = hashMapOf("from" to event)

    override fun getTrack() = TrackFactory.withEvent("$BASE_PATH/result/error/remedy")
        .addData(data.toMap())
        .addData(body).build()

    companion object {
        private const val MODAL = "modal"
        private const val VIEW = "view"
    }
}