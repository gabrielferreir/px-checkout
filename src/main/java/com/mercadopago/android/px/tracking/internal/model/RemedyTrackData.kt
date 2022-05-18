package com.mercadopago.android.px.tracking.internal.model

internal data class RemedyTrackData(
    private val type: String,
    private val extraInfo: Map<String, String>?,
    private val paymentStatus: String? = null,
    private val paymentStatusDetail: String? = null,
    private val index: Int = 0
) : TrackingMapModel()