package com.mercadopago.android.px.internal.base

import com.mercadopago.android.px.tracking.internal.MPTracker

internal abstract class BasePresenterWithState<V: MvpView, S: BaseState>(mpTracker: MPTracker)
    : BasePresenter<V>(mpTracker) {

    private var internalState: S? = null
    val state: S
        get() {
            if (internalState == null) {
                internalState = initState()
            }
            return internalState!!
        }

    abstract fun initState(): S
    fun restoreState(state: S) {
        internalState = state
    }
    protected fun resetState() {
        internalState = null
    }
}
