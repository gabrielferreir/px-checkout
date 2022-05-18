package com.mercadopago.android.px.configuration

data class ModalContent @JvmOverloads constructor(
    val description: Text,
    val button: Button,
    val title: Text? = null,
    val imageUrl: String? = null
)
