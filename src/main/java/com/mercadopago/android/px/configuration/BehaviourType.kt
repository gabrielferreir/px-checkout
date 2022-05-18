package com.mercadopago.android.px.configuration

import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(BehaviourType.START_CHECKOUT, BehaviourType.SWITCH_SPLIT, BehaviourType.TAP_CARD, BehaviourType.TAP_PAY)
annotation class BehaviourType {
    companion object {
        const val START_CHECKOUT = "start_checkout"
        const val SWITCH_SPLIT = "switch_split"
        const val TAP_CARD = "tap_card"
        const val TAP_PAY = "tap_pay"
    }
}
