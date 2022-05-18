package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

internal class ConfirmButtonPressedEvent(private val viewTrackPath: String) : TrackWrapper() {
    override fun getTrack() = TrackFactory.withEvent("$viewTrackPath/pay_button_pressed").build()
    override val shouldTrackExperimentsLabel = false
}
