package com.mercadopago.android.px.internal.extensions

import android.view.Gravity
import com.mercadopago.android.px.model.internal.TextAlignment

fun TextAlignment?.toGravity(): Int = when (this) {
    TextAlignment.CENTER -> Gravity.CENTER_HORIZONTAL
    TextAlignment.RIGHT -> Gravity.END
    TextAlignment.LEFT -> Gravity.START
    else -> Gravity.START
}
