package com.mercadopago.android.px.internal.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

internal fun <T> LiveData<T>.nonNullObserve(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    observe(owner, Observer {
        it?.let(observer)
    })
}

internal fun <T> LiveData<T>.nonNullObserveOnce(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    observe(owner, object : Observer<T> {
        override fun onChanged(value: T?) {
            value?.let(observer)
            removeObserver(this)
        }
    })
}