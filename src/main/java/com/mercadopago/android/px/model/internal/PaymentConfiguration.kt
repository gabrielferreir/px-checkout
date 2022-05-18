package com.mercadopago.android.px.model.internal

import android.os.Parcel
import android.os.Parcelable
import com.mercadopago.android.px.model.PayerCost

data class PaymentConfiguration(
    val paymentMethodId: String,
    val paymentTypeId: String,
    val customOptionId: String,
    val securityCodeRequired: Boolean,
    val splitPayment: Boolean,
    val payerCost: PayerCost?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(PayerCost::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(paymentMethodId)
        parcel.writeString(paymentTypeId)
        parcel.writeString(customOptionId)
        parcel.writeByte(if (securityCodeRequired) 1 else 0)
        parcel.writeByte(if (splitPayment) 1 else 0)
        parcel.writeParcelable(payerCost, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentConfiguration> {
        override fun createFromParcel(parcel: Parcel): PaymentConfiguration {
            return PaymentConfiguration(parcel)
        }

        override fun newArray(size: Int): Array<PaymentConfiguration?> {
            return arrayOfNulls(size)
        }
    }
}