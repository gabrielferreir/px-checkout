package com.mercadopago.android.px.internal.viewmodel

import android.os.Parcelable
import com.meli.android.carddrawer.model.GenericPaymentMethod
import com.mercadopago.android.px.internal.extensions.isNotNull
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class CardDrawerConfiguration(
    val paymentMethodId: String,
    val paymentCard: PaymentCard?,
    val genericPaymentMethod: GenericPaymentMethod?
) : Parcelable {

    init {
        check(paymentCard.isNotNull() || genericPaymentMethod.isNotNull())
    }
}
