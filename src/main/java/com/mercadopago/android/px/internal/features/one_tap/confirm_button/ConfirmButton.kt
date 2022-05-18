package com.mercadopago.android.px.internal.features.one_tap.confirm_button

import com.mercadopago.android.px.internal.features.explode.ExplodingFragment
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.PaymentConfiguration

internal interface ConfirmButton {
    interface View : ExplodingFragment.Handler {
        fun isExploding(): Boolean
        fun addOnStateChange(stateChange: StateChange)
        fun enable()
        fun disable()
    }

    interface ViewModel {
        fun onButtonPressed()
        fun onAnimationFinished()
    }

    interface Handler {
        fun getViewTrackPath(callback: ViewTrackPathCallback)
        fun onPreProcess(callback: OnReadyForProcessCallback)
        @JvmDefault
        fun onEnqueueProcess(callback: OnEnqueueResolvedCallback) = callback.success()
        @JvmDefault
        fun onProcessExecuted(configuration: PaymentConfiguration) = Unit
        @JvmDefault
        fun onProcessFinished(callback: OnPaymentFinishedCallback) = callback.call()
        @JvmDefault
        fun onProcessError(error: MercadoPagoError) = Unit
    }

    interface ViewTrackPathCallback {
        fun call(viewTrackPath: String)
    }

    interface OnReadyForProcessCallback {
        fun call(paymentConfiguration: PaymentConfiguration)
    }

    interface OnEnqueueResolvedCallback {
        fun success()
        fun failure(error: MercadoPagoError)
    }

    interface OnPaymentFinishedCallback {
        fun call()
    }

    interface StateChange {
        fun overrideStateChange(uiState: State): Boolean
    }

    enum class State {
        IN_PROGRESS,
        ENABLE,
        DISABLE
    }
}
