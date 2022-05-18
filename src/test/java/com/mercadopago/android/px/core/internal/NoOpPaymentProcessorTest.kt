package com.mercadopago.android.px.core.internal

import android.app.Application
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.preferences.CheckoutPreference
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NoOpPaymentProcessorTest {

    @Mock
    private lateinit var checkoutPref: CheckoutPreference

    @Mock
    private lateinit var context: Application

    @Mock
    private lateinit var checkoutData: SplitPaymentProcessor.CheckoutData

    @Test(expected = UnsupportedOperationException::class)
    fun testShowFragmentShouldThrowException() {
        val noOpPaymentProcessor = NoOpPaymentProcessor()
        noOpPaymentProcessor.shouldShowFragmentOnPayment(checkoutPref)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testStartPaymentShouldThrowException() {
        val noOpPaymentProcessor = NoOpPaymentProcessor()
        noOpPaymentProcessor.startPayment(context,
            checkoutData,
            object: SplitPaymentProcessor.OnPaymentListener {
                override fun onPaymentFinished(payment: IPaymentDescriptor) {}
                override fun onPaymentError(error: MercadoPagoError) {}
            }
        )
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testGetPaymentTimeoutShouldThrowException() {
        val noOpPaymentProcessor = NoOpPaymentProcessor()
        noOpPaymentProcessor.getPaymentTimeout(checkoutPref)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testSupportsSplitShouldThrowException() {
        val noOpPaymentProcessor = NoOpPaymentProcessor()
        noOpPaymentProcessor.supportsSplitPayment(checkoutPref)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testSupportsGetFragmentShouldThrowException() {
        val noOpPaymentProcessor = NoOpPaymentProcessor()
        noOpPaymentProcessor.getFragment(checkoutData, context)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testShouldSkipUserConfirmationShouldThrowException() {
        val noOpPaymentProcessor = NoOpPaymentProcessor()
        noOpPaymentProcessor.shouldSkipUserConfirmation()
    }
}