package com.mercadopago.android.px.internal.font

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.widget.TextView
import com.mercadolibre.android.andesui.font.Font
import com.mercadolibre.android.andesui.font.TypefaceHelper

object TypefaceHelperWrapper {

    fun getFontTypeface(context: Context, font: Font): Typeface? {
        var typeface: Typeface? = null
        try {
            typeface = TypefaceHelper.getFontTypeface(context, font)
        } catch (e: AbstractMethodError) {
            logError()
        }
        return typeface
    }

    fun <V : TextView> setTypeface(view: V, font: Font) {
        try {
            TypefaceHelper.setTypeface(view, font)
        } catch (e: AbstractMethodError) {
            logError()
        }
    }

    private fun logError() = Log.d("TypefaceHelper", "FontConfigurer is not properly configured")
}
