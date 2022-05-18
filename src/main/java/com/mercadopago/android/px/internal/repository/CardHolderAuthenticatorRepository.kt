package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.addons.model.ThreeDSDataOnlyParams
import com.mercadopago.android.px.model.PaymentData

internal interface CardHolderAuthenticatorRepository {

    suspend fun authenticate(paymentData: PaymentData, threeDSDataOnlyParams: ThreeDSDataOnlyParams): Any
}