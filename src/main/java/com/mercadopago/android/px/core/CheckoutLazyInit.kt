package com.mercadopago.android.px.core

import android.content.Context
import com.mercadopago.android.px.core.internal.PrefetchService
import com.mercadopago.android.px.internal.di.Session

/**
 * CheckoutLazyInit allows you to prefetch [MercadoPagoCheckout] information. Using this Lazy Builder you can
 * avoid having a loading after call [MercadoPagoCheckout.startPayment]
 *
 * @param builder Checkout builder to prefetch
 */
abstract class CheckoutLazyInit protected constructor(private val builder: MercadoPagoCheckout.Builder) {

    @Deprecated("Context no longer needed", ReplaceWith("fetch()"))
    fun fetch(context: Context?) {
        fetch()
    }

    /**
     * Starts fetch for [MercadoPagoCheckout]
     */
    fun fetch() {
        prefetchService = PrefetchService(builder.build(), Session.getInstance(), this).also {
            it.prefetch()
        }
    }

    @Deprecated("Not for public use", ReplaceWith("cancel()"))
    fun fail() {
        cancel()
    }

    internal fun failure() {
        fail(builder.build())
    }

    fun cancel() {
        prefetchService?.cancel()
    }

    /**
     * If prefetch fails this method will be called
     *
     * @param mercadoPagoCheckout served, if you start it anyway it will fail or show a loading depending on the cause.
     */
    abstract fun fail(mercadoPagoCheckout: MercadoPagoCheckout)

    /**
     * If prefetch is success this method will be called and will serve a [MercadoPagoCheckout] instance.
     *
     * @param mercadoPagoCheckout instance for you start the checkout process.
     */
    abstract fun success(mercadoPagoCheckout: MercadoPagoCheckout)

    companion object {
        private var prefetchService: PrefetchService? = null
    }
}
