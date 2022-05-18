package com.mercadopago.android.px.internal.features.checkout

import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.Payment

internal class PostCongratsDriver(builder: Builder) {

    private val payment = builder.payment
    private val postPaymentUrls = builder.postPaymentUrls
    private val action = builder.action
    private val customResponseCode = builder.customResponseCode

    fun execute() {
        with(postPaymentUrls) {
            when {
                redirectUrl.isNotNullNorEmpty() -> action.openInWebView(redirectUrl)
                backUrl.isNotNullNorEmpty() -> action.goToLink(backUrl)
            }
        }
        //We only return the Payment object to respect our signature
        action.exitWith(customResponseCode, if (payment is Payment) payment else null)
    }

    class Builder(internal val payment: IPaymentDescriptor?,
        internal val postPaymentUrls: PostPaymentUrlsMapper.Response) {
        internal lateinit var action: Action
        internal var customResponseCode: Int? = null

        fun action(action: Action) = apply { this.action = action }
        fun customResponseCode(customResponseCode: Int?) = apply { this.customResponseCode = customResponseCode }
        fun build() = PostCongratsDriver(this)
    }

    interface Action {
        fun openInWebView(link: String)
        fun goToLink(link: String)
        fun exitWith(customResponseCode: Int?, payment: Payment?)
    }
}
