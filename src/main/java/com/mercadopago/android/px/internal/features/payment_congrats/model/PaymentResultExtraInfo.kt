package com.mercadopago.android.px.internal.features.payment_congrats.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class PaymentResultExtraInfo(
    val details: List<PaymentCongratsText>
) : Parcelable
