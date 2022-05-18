package com.mercadopago.android.px.tracking.internal.views

import com.mercadopago.android.px.model.OfflinePaymentType
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class OfflineMethodsViewTracker(offlinePaymentTypes: List<OfflinePaymentType>) : TrackWrapper() {

    private val data = OfflineMethodsData.createFrom(offlinePaymentTypes)

    override fun getTrack() = TrackFactory.withView(PATH_REVIEW_OFFLINE_METHODS_VIEW).addData(data.toMap()).build()

    companion object {
        const val PATH_REVIEW_OFFLINE_METHODS_VIEW = "$BASE_PATH/review/one_tap/offline_methods"
    }
}