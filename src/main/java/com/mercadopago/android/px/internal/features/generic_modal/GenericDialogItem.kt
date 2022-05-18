package com.mercadopago.android.px.internal.features.generic_modal

import android.os.Parcel
import com.mercadopago.android.px.internal.util.KParcelable
import com.mercadopago.android.px.internal.util.parcelableCreator
import com.mercadopago.android.px.internal.viewmodel.TextLocalized

data class GenericDialogItem(
    val dialogDescription: String,
    val imageUrl: String?,
    val title: TextLocalized,
    val description: TextLocalized,
    val mainAction: Actionable?,
    val secondaryAction: Actionable?) : KParcelable {

    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readParcelable(TextLocalized::class.java.classLoader)!!,
        parcel.readParcelable(TextLocalized::class.java.classLoader)!!,
        parcel.readParcelable(Actionable::class.java.classLoader),
        parcel.readParcelable(Actionable::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeString(dialogDescription)
        writeString(imageUrl)
        writeParcelable(title, flags)
        writeParcelable(description, flags)
        writeParcelable(mainAction, flags)
        writeParcelable(secondaryAction, flags)
    }

    fun hasSecondaryAction() = secondaryAction != null

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::GenericDialogItem)
    }
}