package com.mercadopago.android.px.internal.viewmodel

import android.content.Context
import android.os.Parcel
import androidx.annotation.StringRes
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.util.KParcelable
import com.mercadopago.android.px.internal.util.parcelableCreator
import com.mercadopago.android.px.internal.util.textformatter.SpannableFormatter
import com.mercadopago.android.px.model.internal.Text

data class TextLocalized(
    private val text: Text?,
    @StringRes private val stringRes: Int) : ILocalizedCharSequence, KParcelable {

    private constructor(parcel: Parcel) : this(
        parcel.readParcelable(Text::class.java.classLoader),
        parcel.readInt())

    override fun get(context: Context): CharSequence? {
        if (text != null && text.message.isNotNullNorEmpty()) {
            return SpannableFormatter(context).apply(text)
        } else if (stringRes != 0) {
            return context.getString(stringRes)
        }
        return null
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeParcelable(text, flags)
        writeInt(stringRes)
    }

    companion object {
        @JvmField
        var CREATOR = parcelableCreator(::TextLocalized)
    }
}