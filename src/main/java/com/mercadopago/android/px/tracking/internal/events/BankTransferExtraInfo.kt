package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

internal class BankTransferExtraInfo(
    val externalAccountId: String?,
    val bankName: String?
) : TrackingMapModel()
