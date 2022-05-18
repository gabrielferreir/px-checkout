package com.mercadopago.android.px.internal.mappers

import android.net.Uri
import com.mercadopago.android.px.internal.callbacks.DeepLinkFrom
import java.util.Locale

private const val FROM = "from"

internal class UriToFromMapper : Mapper<Uri, DeepLinkFrom>() {
    override fun map(value: Uri): DeepLinkFrom {
        val from = value.getQueryParameter(FROM) ?: DeepLinkFrom.NONE.value
        return DeepLinkFrom.valueOf(from.toUpperCase(Locale.ROOT))
    }
}
