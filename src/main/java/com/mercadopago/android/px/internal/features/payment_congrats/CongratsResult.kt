package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.Payment

internal open class CongratsResult {
    data class PaymentResult(val paymentModel: PaymentModel) : CongratsResult()
    data class BusinessPaymentResult(val paymentCongratsModel: PaymentCongratsModel) : CongratsResult()
}

internal open class CongratsPaymentResult : CongratsResult() {
    data class SkipCongratsResult(val paymentModel: PaymentModel) : CongratsPaymentResult()
}

internal open class CongratsPostPaymentResult : CongratsResult() {
    object Loading : CongratsPostPaymentResult()
    object ConnectionError : CongratsPostPaymentResult()
    class BusinessError(val message: String? = null) : CongratsPostPaymentResult()
}

internal open class CongratsPostPaymentUrlsResponse : CongratsResult() {
    data class OnGoToLink(val link: String) : CongratsPostPaymentUrlsResponse()
    data class OnOpenInWebView(val link: String) : CongratsPostPaymentUrlsResponse()
    data class OnExitWith(val customResponseCode: Int?, val payment: Payment?) : CongratsPostPaymentUrlsResponse()
}
