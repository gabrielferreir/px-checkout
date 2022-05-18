package com.mercadopago.android.px.tracking.internal.views

import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.ApiErrorData

class ErrorViewTracker(errorMessage: String, mpError: MercadoPagoError) : TrackWrapper() {

    private val data = mutableMapOf<String, Any>().also {
        it["error_message"] = errorMessage
        it["api_error"] = ApiErrorData(mpError).toMap()
    }

    override fun getTrack() = TrackFactory.withView(PATH).addData(data).build()

    companion object {
        private const val PATH = "$BASE_PATH/generic_error"
    }
}