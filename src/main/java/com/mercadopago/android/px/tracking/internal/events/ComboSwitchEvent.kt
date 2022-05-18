package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.ComboSwitchData

internal class ComboSwitchEvent(optionSelected: String) : TrackWrapper() {

    private val eventPath = "$BASE_PATH/combo_switch"
    private val data = ComboSwitchData(optionSelected)

    override fun getTrack() = TrackFactory.withEvent(eventPath).addData(data.toMap()).build()
}
