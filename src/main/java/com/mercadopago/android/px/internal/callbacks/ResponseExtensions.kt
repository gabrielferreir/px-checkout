package com.mercadopago.android.px.internal.callbacks

import com.mercadopago.android.px.internal.extensions.orIfEmpty
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

internal fun <V, R> Response<V, MercadoPagoError>.map(transform: (V) -> R): Response<R, MercadoPagoError> {
    return when (this) {
        is Response.Success -> {
            try {
                success(transform(result))
            } catch (e: Exception) {
                failure(MercadoPagoError(
                    e.localizedMessage.orIfEmpty("transform operation is not supported"),
                    false))
            }
        }
        is Response.Failure -> failure(exception)
    }
}

internal fun <V, F : MercadoPagoError> Response<V, MercadoPagoError>.mapError(
    transform: (MercadoPagoError) -> F
): Response<V, F> {
    return when (this) {
        is Response.Success -> success(result)
        is Response.Failure -> failure(transform(exception))
    }
}

inline fun <T, R> Response<T, MercadoPagoError>.next(
    block: (value: T) -> Response<R, MercadoPagoError>
): Response<R, MercadoPagoError> {
    return when (this) {
        is Response.Success -> block(result)
        is Response.Failure -> failure(exception)
    }
}