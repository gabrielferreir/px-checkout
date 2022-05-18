package com.mercadopago.android.px.internal.viewmodel

import android.content.Context
import android.os.Parcelable
import com.mercadopago.android.px.internal.extensions.orIfEmpty
import com.mercadopago.android.px.internal.util.TextUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
internal class LazyString(private val text: CharSequence?, private val resId: Int?, private vararg val args: String) : Parcelable {
    constructor(text: CharSequence?, vararg args: String) : this(text, null, *args)
    constructor(resId: Int?, vararg args: String) : this(null, resId, *args)

    fun get(context: Context): CharSequence {
        val value = text.orIfEmpty(resId?.let { context.getString(it) } ?: "")
        return if (args.isNotEmpty()) {
            TextUtil.format(value.toString(), *args)
        } else {
            value
        }
    }
}