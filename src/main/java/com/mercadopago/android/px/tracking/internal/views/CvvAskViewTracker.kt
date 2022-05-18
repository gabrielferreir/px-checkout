package com.mercadopago.android.px.tracking.internal.views

import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.Reason

class CvvAskViewTracker(card: Card?, paymentMethodType: String, reason: Reason) : TrackWrapper() {

    private val data = mutableMapOf<String, Any?>().also {
        if (card?.paymentMethod != null) {
            it["payment_method_id"] = card.paymentMethod!!.id
            it["card_id"] = card.id
            it["reason"] = reason.name.toLowerCase()
        }
    }
    private val path = PATH + paymentMethodType + ACTION_PATH

    override fun getTrack() = TrackFactory.withView(path).addData(data).build()

    companion object {
        private const val PATH = "$BASE_PATH$PAYMENTS_PATH/select_method/"
        private const val ACTION_PATH = "/cvv"
    }
}