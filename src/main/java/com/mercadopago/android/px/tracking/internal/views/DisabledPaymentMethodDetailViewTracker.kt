package com.mercadopago.android.px.tracking.internal.views

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class DisabledPaymentMethodDetailViewTracker : TrackWrapper() {

    override fun getTrack() = TrackFactory.withView(PATH).build()

    companion object {
        private const val PATH = "$BASE_PATH/review/one_tap/disabled_payment_method_detail"
    }
}