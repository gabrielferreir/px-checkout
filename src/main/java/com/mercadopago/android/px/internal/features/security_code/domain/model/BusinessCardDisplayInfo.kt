package com.mercadopago.android.px.internal.features.security_code.domain.model

import com.mercadopago.android.px.model.CardDisplayInfoType

internal data class BusinessCardDisplayInfo(
    val cardholderName: String,
    val expiration: String,
    val color: String,
    val fontColor: String,
    val issuerId: Long = 0,
    val cardPattern: IntArray,
    val cardPatternMask: String,
    val securityCodeLocation: String,
    val securityCodeLength: Int,
    val lastFourDigits: String,
    val paymentMethodImage: String,
    val issuerImage: String? = null,
    val fontType: String?,
    val paymentMethodImageUrl: String? = null,
    val issuerImageUrl: String? = null,
    val type: CardDisplayInfoType
)