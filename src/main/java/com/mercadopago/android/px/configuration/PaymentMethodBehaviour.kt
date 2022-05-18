package com.mercadopago.android.px.configuration

data class PaymentMethodBehaviour(
    val paymentTypeRules: List<String>? = null,
    val paymentMethodRules: List<String>? = null,
    val sliderTitle: String,
    val behaviours: List<Behaviour>? = null
)
