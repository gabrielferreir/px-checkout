package com.mercadopago.android.px.model.exceptions

import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.tracking.internal.model.Reason
import java.util.Locale

internal data class SecurityCodeRequiredError(
    val reason: Reason,
    val card: Card? = null
) : MercadoPagoError("Error reason: ${reason.name.toLowerCase(Locale.getDefault())}", true)
