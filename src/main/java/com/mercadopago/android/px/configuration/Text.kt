package com.mercadopago.android.px.configuration

data class Text @JvmOverloads constructor(
    val message: String,
    val backgroundColor: String? = null,
    val textColor: String? = null,
    val weight: String? = null
)
