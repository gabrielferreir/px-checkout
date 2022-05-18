package com.mercadopago.android.px.internal.viewmodel

import android.content.Context
import android.os.Parcel
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.util.KParcelable
import com.mercadopago.android.px.internal.util.parcelableCreator

class DisableConfiguration : KParcelable {
    val backgroundColor: Int
    val fontColor: Int

    @JvmOverloads
    constructor(context: Context, @ColorRes backgroundResId: Int = R.color.px_disabled_background) {
        backgroundColor = ContextCompat.getColor(context, backgroundResId)
        fontColor = ContextCompat.getColor(context, R.color.px_disabled_font)
    }

    private constructor(parcel: Parcel) {
        backgroundColor = parcel.readInt()
        fontColor = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(backgroundColor)
        parcel.writeInt(fontColor)
    }

    companion object {
        @JvmField val CREATOR = parcelableCreator(::DisableConfiguration)
    }
}
