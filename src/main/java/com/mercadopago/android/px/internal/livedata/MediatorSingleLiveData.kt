package com.mercadopago.android.px.internal.livedata

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

internal class MediatorSingleLiveData<T> : MediatorLiveData<T>(), SingleLiveData<T> {

    override val tag = "MediatorSingleLiveData"
    override val pendingAtomicBoolean = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {

        if (hasActiveObservers()) {
            Log.w(tag, "Multiple observers registered but only one will be notified of changes.");
        }

        super.observe(owner, Observer { value -> onChange(value, observer) })
    }

    @MainThread
    override fun setValue(value: T?) {
        setPending()
        super.setValue(value)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }
}