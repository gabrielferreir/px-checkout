package com.mercadopago.android.px.tracking.internal.views

import com.mercadopago.android.px.model.DiscountConfigurationModel
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.DiscountInfo

class AppliedDiscountViewTracker(private val discountModel: DiscountConfigurationModel) : TrackWrapper() {

    private val data = mutableMapOf<String, Any?>().also {
        DiscountInfo.with(discountModel.discount, discountModel.campaign, discountModel.isAvailable)?.let { info ->
            it["discount"] = info.toMap()
        }
    }

    override fun getTrack() = TrackFactory.withView(PATH).addData(data).build()

    companion object {
        private const val PATH = "$BASE_PATH$PAYMENTS_PATH/applied_discount"
    }
}