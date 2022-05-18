package com.mercadopago.android.px.internal.viewmodel

import android.content.Context
import androidx.annotation.ColorInt
import com.mercadopago.android.px.internal.font.PxFont

interface ITextDescriptor {
    fun getText(context: Context): CharSequence
    fun getFont(context: Context): PxFont
    @ColorInt fun getTextColor(context: Context): Int
    fun getTextSize(context: Context): Float?
}
