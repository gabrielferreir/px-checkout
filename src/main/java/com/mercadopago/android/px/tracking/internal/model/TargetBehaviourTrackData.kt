package com.mercadopago.android.px.tracking.internal.model

import com.mercadopago.android.px.model.one_tap.CheckoutBehaviour

data class TargetBehaviourTrackData(
    @CheckoutBehaviour.Type private val behaviour: String,
    private val deepLink: String) : TrackingMapModel()