package com.mercadopago.android.px.internal.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

internal class GenericDrawable(@DrawableRes val drawableId: Int) : IDetailDrawable {
    override fun getDrawable(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, drawableId)
    }
}