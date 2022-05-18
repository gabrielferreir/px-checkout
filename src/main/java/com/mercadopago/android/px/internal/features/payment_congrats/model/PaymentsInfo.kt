package com.mercadopago.android.px.internal.features.payment_congrats.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class PaymentsInfo(
    val paymentsInfo: List<PaymentInfo>
) : Parcelable
