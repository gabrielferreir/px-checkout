package com.mercadopago.android.px.internal.util

import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.extensions.isZero
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule

internal fun PaymentTypeChargeRule.isHighlightCharge() = isChargeZero() && message.isNotNullNorEmpty()

internal fun PaymentTypeChargeRule.isChargeZero() = charge().isZero()
