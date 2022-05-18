package com.mercadopago.android.px.internal.features.business_result

import com.mercadopago.android.px.internal.features.payment_result.CongratsAutoReturn
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultFooter
import com.mercadopago.android.px.internal.view.PaymentResultBody
import com.mercadopago.android.px.internal.view.PaymentResultHeader

internal data class BusinessPaymentResultViewModel(
    val headerModel: PaymentResultHeader.Model,
    val bodyModel: PaymentResultBody.Model,
    val footerModel: PaymentResultFooter.Model,
    val autoReturnModel: CongratsAutoReturn.Model? = null
)
