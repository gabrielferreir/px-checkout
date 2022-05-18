package com.mercadopago.android.px.internal.extensions

import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

inline fun <T, F: MercadoPagoError> Response<T, F>.ifSuccess(
    block: (value: T) -> Unit
): Response<T, F> {
    return when (this) {
        is Response.Success -> {
            block(result)
            success(result)
        }
        is Response.Failure -> failure(exception)
    }
}

inline fun <T, F: MercadoPagoError> Response<T, F>.ifFailure(
    block: (value: F) -> Unit
): Response<T, F> {
    return when (this) {
        is Response.Success -> success(result)
        is Response.Failure -> {
            block(exception)
            failure(exception)
        }
    }
}
