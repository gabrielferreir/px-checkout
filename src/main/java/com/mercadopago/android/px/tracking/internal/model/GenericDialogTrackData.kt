package com.mercadopago.android.px.tracking.internal.model

import java.util.*

sealed class GenericDialogTrackData(private val description: String, hasSecondaryAction: Boolean) : TrackingMapModel() {
    val actions = if (hasSecondaryAction) 2 else 1

    class Open(description: String, hasSecondaryAction: Boolean) :
        GenericDialogTrackData(description, hasSecondaryAction)

    class Dismiss(description: String, hasSecondaryAction: Boolean) :
        GenericDialogTrackData(description, hasSecondaryAction)

    class Action(
        private val deepLink: String,
        type: Type,
        description: String,
        hasSecondaryAction: Boolean) : GenericDialogTrackData(description, hasSecondaryAction) {

        val type = type.name.toLowerCase(Locale.ROOT)
    }

    enum class Type {
        MAIN_ACTION,
        SECONDARY_ACTION
    }
}