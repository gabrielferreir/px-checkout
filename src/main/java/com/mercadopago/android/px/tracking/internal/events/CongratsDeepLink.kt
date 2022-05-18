package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

abstract class CongratsDeepLink : TrackWrapper() {
    protected abstract val congratsType: String
    protected abstract val deepLinkType: DeepLinkType
    protected abstract val deepLink: String

    private fun getPath() = "$BASE_PATH/result/$congratsType/deep_link"
    private fun getData() = mutableMapOf("type" to deepLinkType.type, "deep_link" to deepLink)

    override fun getTrack() = TrackFactory.withEvent(getPath()).addData(getData()).build()
}