package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.model.AmountConfiguration
import com.mercadopago.android.px.model.ExpressMetadata
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.ExpressInstallmentsData

class InstallmentsEventTrack(expressMetadata: ExpressMetadata, amountConfiguration: AmountConfiguration) : TrackWrapper() {

    private val data = ExpressInstallmentsData.createFrom(expressMetadata, amountConfiguration)

    override fun getTrack() = TrackFactory.withEvent(PATH).addData(data.toMap()).build()

    companion object {
        private const val PATH = "$BASE_PATH/review/one_tap/installments"
    }
}