package com.mercadopago.android.px.core

import androidx.annotation.Size
import com.mercadopago.android.px.core.v2.PaymentProcessor
import com.mercadopago.android.px.internal.model.SecurityType
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.preferences.CheckoutPreference

interface SplitPaymentProcessor : PaymentProcessor {

    open class CheckoutData @JvmOverloads constructor(
        @JvmField @param:Size(min = 1) val paymentDataList: List<PaymentData>,
        @JvmField val checkoutPreference: CheckoutPreference,
        @JvmField val securityType: String,
        @JvmField val validationProgramId: String? = null
    ) {

        @Deprecated("use the one with security type")
        constructor(
            @Size(min = 1) paymentData: List<PaymentData>,
            checkoutPreference: CheckoutPreference
        ) : this(paymentData, checkoutPreference, SecurityType.NONE.value)
    }

    interface OnPaymentListener {
        fun onPaymentFinished(payment: IPaymentDescriptor)
        fun onPaymentError(error: MercadoPagoError)
    }

    interface BackHandler {
        /**
         * The implementation of this method can tell you if the back action is enabled or disabled. In certain cases
         * you want to block UI, disabling the back button. This occurs for toolbar back button, display screen back
         * button and swipe back gestures.
         */
        val isBackEnabled: Boolean
    }
}