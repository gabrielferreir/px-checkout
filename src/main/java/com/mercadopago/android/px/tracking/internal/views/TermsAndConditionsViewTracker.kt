package com.mercadopago.android.px.tracking.internal.views

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class TermsAndConditionsViewTracker(url: String) : TrackWrapper() {

    private val data = mutableMapOf<String, Any>().also {
        it["url"] = url
    }

    override fun getTrack() = TrackFactory.withView(PATH).addData(data).build()

    companion object {
        private const val PATH = "$BASE_PATH/payments/terms_and_conditions"
    }
}