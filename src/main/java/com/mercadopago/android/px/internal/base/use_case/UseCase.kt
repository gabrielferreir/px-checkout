package com.mercadopago.android.px.internal.base.use_case

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.orIfEmpty
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias CallBack<T> = (T) -> Unit

abstract class UseCase<in P, out R>(protected val tracker: MPTracker) {

    protected abstract val contextProvider: CoroutineContextProvider
    protected abstract suspend fun doExecute(param: P): Response<R, MercadoPagoError>

    @JvmOverloads
    fun execute(param: P, success: CallBack<R> = {}, failure: CallBack<MercadoPagoError> = {}) {
        CoroutineScope(contextProvider.IO).launch {
            try {
                doExecute(param).also { response ->
                    withContext(contextProvider.Main) {
                        response.resolve(success, failure)
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage.orIfEmpty(
                    "Error when execute ${this@UseCase.javaClass.simpleName}")
                withContext(contextProvider.Main) {
                    val error = MercadoPagoError(errorMessage, false)
                    tracker.track(FrictionEventTracker.with(
                        "/use_case",
                        FrictionEventTracker.Id.EXECUTE_USE_CASE,
                        FrictionEventTracker.Style.SCREEN,
                        error))
                    failure(error)
                }
            }
        }
    }

    suspend fun execute(param: P): Response<R, MercadoPagoError> {
        return runCatching { doExecute(param) }.getOrElse {
            val errorMessage = it.localizedMessage.orIfEmpty(
                "Error when execute ${this@UseCase.javaClass.simpleName}")
            val error = MercadoPagoError(errorMessage, false)
            withContext(contextProvider.Main) {
                tracker.track(FrictionEventTracker.with(
                    "/use_case",
                    FrictionEventTracker.Id.EXECUTE_USE_CASE,
                    FrictionEventTracker.Style.SCREEN,
                    error))
            }
            Response.Failure(error)
        }
    }
}
