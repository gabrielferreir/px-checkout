package com.mercadopago.android.px.internal.base

import com.mercadopago.android.px.tracking.internal.MPTracker

internal abstract class BaseViewModelWithState<S: BaseState>(mpTracker: MPTracker): BaseViewModel(mpTracker) {

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
        onStateRestored()
    }
    protected open fun onStateRestored() = Unit
    protected fun resetState() {
        internalState = null
    }
}
