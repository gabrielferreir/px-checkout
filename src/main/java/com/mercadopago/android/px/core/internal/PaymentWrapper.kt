package com.mercadopago.android.px.core.internal

import android.os.Parcel
import android.os.Parcelable
import com.mercadopago.android.px.internal.util.KParcelable
import com.mercadopago.android.px.internal.util.parcelableCreator
import com.mercadopago.android.px.model.IParcelablePaymentDescriptor
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.Payment

class PaymentWrapper(private val payment: IPaymentDescriptor) : KParcelable {

    constructor(parcel: Parcel): this(
        if (parcel.readInt() == 1) {
            parcel.readParcelable<IParcelablePaymentDescriptor>(IParcelablePaymentDescriptor::class.java.classLoader)!!
        } else {
            parcel.readSerializable() as IPaymentDescriptor
        }
    )

    fun get() = payment
    fun isStatusDetailRecoverable() = Payment.StatusDetail.isStatusDetailRecoverable(payment.paymentStatusDetail)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        if (payment is Parcelable) {
            parcel.writeInt(1)
            parcel.writeParcelable(payment, flags)
        } else {
            parcel.writeInt(0)
            parcel.writeSerializable(payment)
        }
    }

    companion object {
        @JvmField val CREATOR = parcelableCreator(::PaymentWrapper)
    }
}
