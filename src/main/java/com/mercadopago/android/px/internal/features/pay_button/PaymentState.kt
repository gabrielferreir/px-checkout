package com.mercadopago.android.px.internal.features.pay_button

import android.os.Parcelable
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.tracking.internal.model.Reason
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class PaymentState(
    val paymentConfiguration: PaymentConfiguration,
    val card: Card? = null,
    val paymentRecovery: PaymentRecovery? = null,
    val reason: Reason? = null
) : Parcelable
