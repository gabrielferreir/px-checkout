package com.mercadopago.android.px

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.use_case.UseCase
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class TestContextProvider() : CoroutineContextProvider() {
    override var Main: CoroutineContext = Dispatchers.Unconfined
    override var IO: CoroutineContext = Dispatchers.Unconfined
    override var Default: CoroutineContext = Dispatchers.Unconfined

    constructor(io: CoroutineContext, main: CoroutineContext) : this() {
        Main = main
        IO = io
    }

}