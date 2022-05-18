package com.mercadopago.android.px

import com.mercadopago.android.px.internal.base.use_case.CallBack

interface CallbackTest<T> : CallBack<T> {
    override operator fun invoke(value: T)
}