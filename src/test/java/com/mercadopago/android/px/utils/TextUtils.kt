package com.mercadopago.android.px.utils

import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.internal.Text

object TextUtils {
    fun getText(text: String, weight: String = "regular", color: String = "#000000"): Text {
        return JsonUtil.fromJson(
            """{
                "message": "$text",
                "weight": "$weight",
                "text_color": "$color"
            }""".trimIndent(), Text::class.java
        )!!
    }
}