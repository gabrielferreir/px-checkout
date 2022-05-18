package com.mercadopago.android.px.internal.features.payment_result.remedies

internal enum class RemedyType(private val type: String) {
    PAYMENT_METHOD_SUGGESTION("payment_method_suggestion"),
    CVV_REQUEST("cvv_request"),
    KYC_REQUEST("kyc_request");

    fun getType() = type
}