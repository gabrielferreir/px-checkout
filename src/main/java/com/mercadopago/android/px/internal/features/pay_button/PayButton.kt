package com.mercadopago.android.px.internal.features.pay_button

import android.content.Intent
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction
import com.mercadopago.android.px.model.PaymentRecovery

internal interface PayButton {

    interface View : ConfirmButton.View {
        fun stimulate()
    }

    interface ViewModel : ConfirmButton.ViewModel {
        fun preparePayment()
        fun handleAuthenticationResult(isSuccess: Boolean, securityRequested: Boolean)
        fun startPayment()
        fun recoverPayment()
        fun onRecoverPaymentEscInvalid(recovery: PaymentRecovery)
        fun onPostPayment(paymentModel: PaymentModel)
        fun onPostPaymentAction(postPaymentAction: PostPaymentAction)
        fun skipRevealAnimation(): Boolean
        fun handleCongratsResult(resultCode: Int, data: Intent?)
        fun handleSecurityCodeResult(resultCode: Int, data: Intent?)
        fun onResultIconAnimation()
    }

    interface Handler : ConfirmButton.Handler {
        @JvmDefault fun onPostPaymentAction(postPaymentAction: PostPaymentAction) = Unit
        @JvmDefault fun onCvvRequested(paymentState: PaymentState) = Unit
        @JvmDefault fun onPostCongrats(resultCode: Int, data: Intent?) = Unit
    }
}
