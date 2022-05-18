package com.mercadopago.android.px.internal.util

import android.util.Log
import com.mercadopago.android.px.BuildConfig

object Logger {

    @JvmStatic
    fun debug(tag: String, data: Any) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, JsonUtil.toJson(data))
        }
    }

    @JvmStatic
    fun debug(tag: String, data: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, data)
        }
    }

    @JvmStatic
    fun debug(tag: String, throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, TextUtil.EMPTY, throwable)
        }
    }
}