package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

internal class ChangePaymentMethodEvent(showedModal: Boolean) : TrackWrapper() {

    private val event = if(showedModal) MODAL else VIEW
    private val body = hashMapOf("from" to event)

    override fun getTrack() = TrackFactory.withEvent(PATH).addData(body).build()

    companion object {
        private const val PATH = "${BASE_PATH}/result/error/change_payment_method"
        private const val MODAL = "modal"
        private const val VIEW = "view"
    }
}