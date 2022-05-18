package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.ProgramValidationData

internal class ProgramValidationEvent(validationProgramId: String?) : TrackWrapper() {

    private val eventPath = "$BASE_PATH/program_validation"
    private val data = ProgramValidationData(validationProgramId)

    override fun getTrack() = TrackFactory.withEvent(eventPath).addData(data.toMap()).build()
}
