package com.mercadopago.android.px.internal.features.payment_result.remedies

import android.os.Parcel
import android.os.Parcelable

data class RemediesPayerCost(val payerCostIndex: Int, val installments: Int): Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(payerCostIndex)
        parcel.writeInt(installments)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<RemediesPayerCost> {
        override fun createFromParcel(parcel: Parcel) = RemediesPayerCost(parcel)
        override fun newArray(size: Int) = arrayOfNulls<RemediesPayerCost>(size)
    }
}