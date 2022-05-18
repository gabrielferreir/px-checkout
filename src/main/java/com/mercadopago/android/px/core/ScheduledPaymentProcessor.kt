package com.mercadopago.android.px.core

import com.mercadopago.android.px.core.v2.PaymentProcessor
import com.mercadopago.android.px.preferences.CheckoutPreference

abstract class ScheduledPaymentProcessor : PaymentProcessor {
    final override fun supportsSplitPayment(checkoutPreference: CheckoutPreference?): Boolean = false
}
