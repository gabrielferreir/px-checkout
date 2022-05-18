package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.addons.model.internal.Experiment
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.ConfirmData

private const val EVENT_PATH_REVIEW_CONFIRM = "${TrackWrapper.BASE_PATH}/review/confirm"

class ConfirmEvent @JvmOverloads constructor(private val data: ConfirmData,
    private val experiments: List<Experiment> = emptyList()) : TrackWrapper() {

    override fun getTrack() = TrackFactory.withEvent(EVENT_PATH_REVIEW_CONFIRM)
        .addData(data.toMap())
        .addExperiments(experiments)
        .build()
}