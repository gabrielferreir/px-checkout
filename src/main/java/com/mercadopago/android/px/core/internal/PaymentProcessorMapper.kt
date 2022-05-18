package com.mercadopago.android.px.core.internal

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.PaymentProcessor
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.preferences.CheckoutPreference

internal class PaymentProcessorMapper(private val paymentListenerMapper: PaymentListenerMapper,
    private val checkoutDataMapper: CheckoutDataMapper) : Mapper<PaymentProcessor, SplitPaymentProcessor>() {

    override fun map(paymentProcessor: PaymentProcessor): SplitPaymentProcessor {
        return MappedPaymentProcessor(paymentProcessor, paymentListenerMapper, checkoutDataMapper)
    }

    private class MappedPaymentProcessor : SplitPaymentProcessor {
        private lateinit var paymentProcessor: PaymentProcessor
        private lateinit var paymentListenerMapper: PaymentListenerMapper
        private lateinit var checkoutDataMapper: CheckoutDataMapper
        private val createdFromParcel: Boolean

        constructor(paymentProcessor: PaymentProcessor, paymentListenerMapper: PaymentListenerMapper,
            checkoutDataMapper: CheckoutDataMapper) {
            createdFromParcel = false
            this.paymentProcessor = paymentProcessor
            this.paymentListenerMapper = paymentListenerMapper
            this.checkoutDataMapper = checkoutDataMapper
        }

        private constructor(parcel: Parcel) {
            createdFromParcel = true
        }

        override fun startPayment(context: Context, data: SplitPaymentProcessor.CheckoutData,
            paymentListener: SplitPaymentProcessor.OnPaymentListener) {
            if (createdFromParcel) {
                paymentListener.onPaymentError(MercadoPagoError.createNotRecoverable(context.getString(R.string.px_error_title)))
            } else {
                paymentProcessor.startPayment(
                    checkoutDataMapper.map(data), context, paymentListenerMapper.map(paymentListener))
            }
        }

        override fun getPaymentTimeout(checkoutPreference: CheckoutPreference): Int {
            return if (createdFromParcel) 60 else paymentProcessor.paymentTimeout
        }

        override fun shouldShowFragmentOnPayment(checkoutPreference: CheckoutPreference): Boolean {
            return !createdFromParcel && paymentProcessor.shouldShowFragmentOnPayment()
        }

        override fun supportsSplitPayment(checkoutPreference: CheckoutPreference?) = false

        override fun getFragment(data: SplitPaymentProcessor.CheckoutData, context: Context): Fragment? {
            return if (createdFromParcel) {
                null
            } else {
                val mapped = checkoutDataMapper.map(data)
                val fragment = paymentProcessor.getFragment(checkoutDataMapper.map(data), context)
                val fragmentBundle = paymentProcessor.getFragmentBundle(mapped, context)
                // Do not remove checks, vending imp returns null
                if (fragment != null && fragmentBundle != null) {
                    fragment.arguments = fragmentBundle
                }
                fragment
            }
        }

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = Unit

        companion object {
            @JvmField val CREATOR = object : Parcelable.Creator<MappedPaymentProcessor> {
                override fun createFromParcel(parcel: Parcel) = MappedPaymentProcessor(parcel)
                override fun newArray(i: Int) = arrayOfNulls<MappedPaymentProcessor>(0)
            }
        }
    }
}