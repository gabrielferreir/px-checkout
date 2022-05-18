package com.mercadopago.android.px.core.v2

import android.content.Context
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.preferences.CheckoutPreference

interface PaymentProcessor : Parcelable {

    /**
     * Method that we will call if [.shouldShowFragmentOnPayment] is false. we will place a
     * loading for you meanwhile we call this method.
     *
     * @param data            checkout data to the moment it's called.
     * @param context         that you may need to fill information.
     * @param paymentListener when you have processed your payment you should call [OnPaymentListener]
     */
    fun startPayment(
        context: Context, data: SplitPaymentProcessor.CheckoutData,
        paymentListener: SplitPaymentProcessor.OnPaymentListener
    )

    /**
     * If you how much time will take the request timeout you can tell us to optimize the loading UI. will only works if
     * [.shouldShowFragmentOnPayment] is false.
     *
     * @return time in milliseconds
     */
    fun getPaymentTimeout(checkoutPreference: CheckoutPreference): Int

    /**
     * method to know if the payment processor should pay through a fragment or do it through background execution. will
     * be called on runtime.
     *
     * @return if it should show view
     */
    fun shouldShowFragmentOnPayment(checkoutPreference: CheckoutPreference): Boolean

    /**
     * Method used as a flag to know if we should offer split payment or not to the user.
     *
     * @return if it should show view
     */
    fun supportsSplitPayment(checkoutPreference: CheckoutPreference?): Boolean

    /**
     * Fragment that will appear if [.shouldShowFragmentOnPayment] is true when user clicks
     * this payment method.
     *
     *
     * inside [Fragment.onAttach] context will be an instance of [ ]
     *
     * @param data    checkout data to the moment it's called.
     * @param context that you may need to fill information.
     * @return fragment
     */
    fun getFragment(data: SplitPaymentProcessor.CheckoutData, context: Context): Fragment?

    /**
     * If the boolean is true payment processor's fragment will be showed instead review and confirm screen
     *
     * @return if fragment should be showed
     */
    @JvmDefault
    fun shouldSkipUserConfirmation(): Boolean {
        return false
    }
}