package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

private const val EVENT_PATH_POST_PAYMENT_FLOW = "${TrackWrapper.BASE_PATH}/post_payment_flow"
private const val DESTINATION_KEY = "destination"

class PostPaymentFlowEvent @JvmOverloads constructor(private val deepLink: String) : TrackWrapper() {

    override fun getTrack() = TrackFactory.withEvent(EVENT_PATH_POST_PAYMENT_FLOW)
        .addData(mapOf(DESTINATION_KEY to deepLink))
        .build()
}
