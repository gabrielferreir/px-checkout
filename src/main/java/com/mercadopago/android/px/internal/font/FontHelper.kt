package com.mercadopago.android.px.internal.font

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.HandlerThread
import android.util.SparseArray
import android.widget.TextView
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.extensions.isNotNull

object FontHelper {
    private const val FONT_ROBOTO_MONO = "Roboto Mono"
    private const val PROVIDER_AUTHORITY = "com.google.android.gms.fonts"
    private const val PROVIDER_PACKAGE = "com.google.android.gms"

    private val CACHE = SparseArray<Typeface>()
    private val HANDLER = Handler(HandlerThread("fonts").run {
        start()
        looper
    })
    private var initialized = false

    @JvmStatic
    fun init(context: Context) {
        if (!initialized) {
            initialized = true
            fetchFont(context, PxFont.MONOSPACE.id, FONT_ROBOTO_MONO, Typeface.MONOSPACE)
        }
    }

    @JvmStatic
    fun setFont(view: TextView, font: PxFont) {
        val typeface = CACHE[font.id]
        when {
            typeface.isNotNull() -> view.typeface = typeface
            font.font.isNotNull() -> TypefaceHelperWrapper.setTypeface(view, font.font)
        }
    }

    @JvmStatic
    fun getFont(context: Context, font: PxFont) =
        CACHE[font.id] ?: TypefaceHelperWrapper.getFontTypeface(context, font.font)

    private fun fetchFont(context: Context, id: Int, fontName: String, fallback: Typeface?) {
        val callback: FontsContractCompat.FontRequestCallback = object : FontsContractCompat.FontRequestCallback() {
            override fun onTypefaceRetrieved(typeface: Typeface) {
                CACHE.put(id, typeface)
            }

            override fun onTypefaceRequestFailed(reason: Int) {
                fallback?.let { CACHE.put(id, it) }
            }
        }
        FontsContractCompat.requestFont(context,
            FontRequest(PROVIDER_AUTHORITY, PROVIDER_PACKAGE, fontName, R.array.com_google_android_gms_fonts_certs),
            callback, HANDLER)
    }
}
