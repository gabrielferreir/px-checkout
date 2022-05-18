package com.mercadopago.android.px.internal.features.pay_button

import com.mercadopago.android.px.model.IParcelablePaymentDescriptor

internal data class PostPaymentFlowStarted(
    val iParcelablePaymentDescriptor: IParcelablePaymentDescriptor,
    val postPaymentDeepLinkUrl: String
)
