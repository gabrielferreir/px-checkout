package com.mercadopago.android.px.configuration

import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(PaymentMethodRules.IGNORE_INSUFFICIENT_AM_BALANCE)
annotation class PaymentMethodRules {
    companion object {
        const val IGNORE_INSUFFICIENT_AM_BALANCE = "ignore_insufficient_am_balance"
    }
}
