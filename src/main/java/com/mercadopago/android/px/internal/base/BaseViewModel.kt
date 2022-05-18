package com.mercadopago.android.px.internal.base

import androidx.lifecycle.ViewModel
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.TrackWrapper

abstract class BaseViewModel(protected val tracker: MPTracker) : ViewModel() {
    fun track(trackWrapper: TrackWrapper) {
        tracker.track(trackWrapper)
    }
}
