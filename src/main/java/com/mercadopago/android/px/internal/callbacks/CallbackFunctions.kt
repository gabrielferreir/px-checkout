package com.mercadopago.android.px.internal.callbacks

import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun <T> MPCall<T>.awaitTaggedCallback(requestOrigin: String): Response<T, MercadoPagoError> =
    suspendCancellableCoroutine { cont ->
        enqueue(object : TaggedCallback<T>(requestOrigin) {
            override fun onSuccess(result: T) {
                cont.resume(Response.Success(result))
            }

            override fun onFailure(error: MercadoPagoError?) {
                error?.let {
                    cont.resume(Response.Failure(it))
                }
            }
        })
    }