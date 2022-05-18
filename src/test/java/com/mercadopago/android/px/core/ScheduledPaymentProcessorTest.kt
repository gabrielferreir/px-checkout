package com.mercadopago.android.px.core

import android.content.Context
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.preferences.CheckoutPreference
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ScheduledPaymentProcessorTest {

    @Mock
    private lateinit var checkoutPreference: CheckoutPreference

    @Test
    fun supportSplitPaymentShouldReturnFalse() {
        val processor = ScheduledPaymentProcessorImpl()
        assertFalse(processor.supportsSplitPayment(checkoutPreference))
    }

    class ScheduledPaymentProcessorImpl : ScheduledPaymentProcessor() {
        override fun startPayment(
            context: Context,
            data: SplitPaymentProcessor.CheckoutData,
            paymentListener: SplitPaymentProcessor.OnPaymentListener
        ) {
            TODO("Not yet implemented")
        }

        override fun getPaymentTimeout(checkoutPreference: CheckoutPreference): Int {
            TODO("Not yet implemented")
        }

        override fun shouldShowFragmentOnPayment(checkoutPreference: CheckoutPreference): Boolean {
            TODO("Not yet implemented")
        }

        override fun getFragment(data: SplitPaymentProcessor.CheckoutData, context: Context): Fragment? {
            TODO("Not yet implemented")
        }

        override fun describeContents(): Int {
            TODO("Not yet implemented")
        }

        override fun writeToParcel(p0: Parcel?, p1: Int) {
            TODO("Not yet implemented")
        }
    }
}