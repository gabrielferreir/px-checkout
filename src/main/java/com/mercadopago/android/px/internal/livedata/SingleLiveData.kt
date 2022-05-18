package com.mercadopago.android.px.internal.livedata

import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

interface SingleLiveData<T> {
    val tag: String
    val pendingAtomicBoolean: AtomicBoolean

    fun onChange(value: T?, observer: Observer<in T?>) {
        if (pendingAtomicBoolean.compareAndSet(true, false)) {
            observer.onChanged(value)
        }
    }

    fun setPending() {
        pendingAtomicBoolean.set(true)
    }
}