package com.mercadopago.android.px.tracking.internal.events

class CongratsSuccessDeepLink(override val deepLinkType: DeepLinkType, override val deepLink: String) : CongratsDeepLink() {
    override val congratsType = "success"
}