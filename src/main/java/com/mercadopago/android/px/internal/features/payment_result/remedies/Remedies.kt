package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultButton

internal interface Remedies {
    interface View {
        fun onPrePayment(callback: ConfirmButton.OnReadyForProcessCallback)
        fun onPayButtonPressed(callback: ConfirmButton.OnEnqueueResolvedCallback)
    }

    interface ViewModel {
        fun onPrePayment(callback: ConfirmButton.OnReadyForProcessCallback)
        fun onPayButtonPressed(callback: ConfirmButton.OnEnqueueResolvedCallback)
        fun onCvvFilled(cvv: String)
        fun onButtonPressed(action: PaymentResultButton.Action)
    }
}
