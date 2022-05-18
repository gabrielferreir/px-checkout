package com.mercadopago.android.px.internal.features.security_code.data

import com.mercadopago.android.px.internal.viewmodel.LazyString
import com.mercadopago.android.px.model.CardDisplayInfo

internal data class SecurityCodeDisplayData(
    val title: LazyString,
    val message: LazyString,
    val securityCodeLength: Int,
    val cardDisplayInfo: CardDisplayInfo? = null
)