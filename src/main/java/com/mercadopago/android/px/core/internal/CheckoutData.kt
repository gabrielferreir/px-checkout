package com.mercadopago.android.px.core.internal

import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.preferences.CheckoutPreference

internal class CheckoutData(
    paymentDataList: List<PaymentData>,
    checkoutPreference: CheckoutPreference,
    securityType: String,
    validationProgramId: String? = null
) : SplitPaymentProcessor.CheckoutData(paymentDataList, checkoutPreference, securityType, validationProgramId)