package com.mercadopago.android.px.core.internal

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.preferences.CheckoutPreference

/**
 * This class is needed for legacy purposes.
 */
internal class NoOpPaymentProcessor() : SplitPaymentProcessor {
    constructor(parcel: Parcel) : this()

    override fun startPayment(
        context: Context,
        data: SplitPaymentProcessor.CheckoutData,
        paymentListener: SplitPaymentProcessor.OnPaymentListener
    ) = throw UnsupportedOperationException()

    override fun getPaymentTimeout(checkoutPreference: CheckoutPreference): Int = throw UnsupportedOperationException()

    override fun shouldShowFragmentOnPayment(checkoutPreference: CheckoutPreference): Boolean =
        throw UnsupportedOperationException()

    override fun supportsSplitPayment(checkoutPreference: CheckoutPreference?): Boolean =
        throw UnsupportedOperationException()

    override fun getFragment(data: SplitPaymentProcessor.CheckoutData, context: Context): Fragment? =
        throw UnsupportedOperationException()

    override fun shouldSkipUserConfirmation(): Boolean = throw java.lang.UnsupportedOperationException()

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<NoOpPaymentProcessor> {
        override fun createFromParcel(parcel: Parcel): NoOpPaymentProcessor {
            return NoOpPaymentProcessor(parcel)
        }

        override fun newArray(size: Int): Array<NoOpPaymentProcessor?> {
            return arrayOfNulls(size)
        }
    }

}