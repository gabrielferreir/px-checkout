package com.mercadopago.android.px.internal.viewmodel

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

internal class GenericColor(@ColorRes val colorId: Int) : IDetailColor {
    override fun getColor(context: Context): Int {
        return ContextCompat.getColor(context, colorId)
    }
}