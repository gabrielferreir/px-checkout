package com.mercadopago.android.px.internal.features.security_code.domain.model

import com.mercadopago.android.px.internal.viewmodel.LazyString

internal data class BusinessSecurityCodeDisplayData(
    val title: LazyString,
    val message: LazyString,
    val securityCodeLength: Int,
    val cardDisplayInfo: BusinessCardDisplayInfo? = null
)