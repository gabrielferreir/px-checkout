package com.mercadopago.android.px.internal.features.security_code

import java.util.Locale

enum class RenderMode {
    HIGH_RES, MEDIUM_RES, LOW_RES, NO_CARD;

    companion object {
        fun from(value: String) : RenderMode {
            values().forEach {
                if (it.name.toLowerCase(Locale.ROOT) == value) {
                    return it
                }
            }
            return NO_CARD
        }
    }
}