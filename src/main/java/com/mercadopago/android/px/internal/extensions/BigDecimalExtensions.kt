package com.mercadopago.android.px.internal.extensions

import java.math.BigDecimal

internal fun BigDecimal.isZero(): Boolean = this.compareTo(BigDecimal.ZERO) == 0
