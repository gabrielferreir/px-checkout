package com.mercadopago.android.px.internal.features.checkout

import android.net.Uri
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.Payment
import com.mercadopago.android.px.preferences.CheckoutPreference
import com.mercadopago.android.px.preferences.PostPaymentUrls

internal object PostPaymentUrlsMapper : Mapper<PostPaymentUrlsMapper.Model, PostPaymentUrlsMapper.Response>() {

    override fun map(model: Model): Response {
        return Response(
            getUrl(model.redirectUrl, model.preference.redirectUrls, model),
            getUrl(model.backUrl, model.preference.backUrls, model)
        )
    }

    private fun getUrl(url: String?, postPaymentUrls: PostPaymentUrls?, model: Model): String? {
        with(model) {
            return url.takeIf { it.isNotNullNorEmpty() } ?: payment?.let { payment ->
                getUrlFromPostPayment(postPaymentUrls, payment.paymentStatus).takeIf { it.isNotNullNorEmpty() }?.let {
                    appendDataToUrl(it, payment, preference, siteId)
                }
            }
        }
    }

    private fun getUrlFromPostPayment(postPaymentUrls: PostPaymentUrls?, paymentStatus: String): String? {
        return postPaymentUrls?.let {
            when(paymentStatus) {
                Payment.StatusCodes.STATUS_APPROVED -> it.success
                Payment.StatusCodes.STATUS_REJECTED -> it.failure
                else -> it.pending
            }
        }
    }

    private fun appendDataToUrl(url: String, payment: IPaymentDescriptor, preference: CheckoutPreference, siteId: String): String {
        return Uri.parse(url).buildUpon()
            .appendQueryParameter("collection_id", payment.id.toString())
            .appendQueryParameter("collection_status", payment.paymentStatus)
            .appendQueryParameter("payment_id", payment.id.toString())
            .appendQueryParameter("status", payment.paymentStatus)
            .appendQueryParameter("payment_type", payment.paymentTypeId)
            .appendQueryParameter("preference_id", preference.id)
            .appendQueryParameter("external_reference", preference.externalReference)
            .appendQueryParameter("site_id", siteId)
            .toString()
    }

    data class Model(
        val redirectUrl: String?,
        val backUrl: String?,
        val payment: IPaymentDescriptor?,
        val preference: CheckoutPreference,
        val siteId: String
    )

    data class Response(
        val redirectUrl: String?,
        val backUrl: String?
    )
}
