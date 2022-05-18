package com.mercadopago.android.px.internal.base

import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.events.AbortEvent
import com.mercadopago.android.px.tracking.internal.events.BackEvent
import java.lang.ref.WeakReference

/**
 * Base class for all `BasePresenter` implementations.
 */
internal abstract class BasePresenter<V : MvpView>(protected val tracker: MPTracker) {

    private var viewReference: WeakReference<V>? = null
    protected var viewTrack: TrackWrapper? = null

    protected val isViewAttached: Boolean
        get() = viewReference?.get() != null
    protected val view: V
        get() = if (!isViewAttached) {
            throw IllegalStateException("view not attached")
        } else {
            viewReference!!.get()!!
        }

    protected fun trackAbort() {
        viewTrack?.let { track(AbortEvent(it)) }
    }

    protected fun trackBack() {
        viewTrack?.let { track(BackEvent(it)) }
    }

    protected fun trackView() {
        viewTrack?.let { track(it) }
    }

    protected fun track(trackWrapper: TrackWrapper) {
        tracker.track(trackWrapper)
    }

    open fun attachView(view: V) {
        viewReference = WeakReference(view)
    }

    fun detachView() {
        viewReference?.clear()
        viewReference = null
    }
}
