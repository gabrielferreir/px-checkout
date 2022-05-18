package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultButton
import com.mercadopago.android.px.internal.viewmodel.LazyString
import com.mercadopago.android.px.model.internal.Button

internal object PaymentResultButtonMapper : Mapper<Button?, PaymentResultButton?>() {

    override fun map(button: Button?) = button?.run {
        PaymentResultButton(
            type?.let { PaymentResultButton.Type.valueOf(it.name) } ?: PaymentResultButton.Type.LOUD,
            LazyString(label),
            action?.let { PaymentResultButton.Action.valueOf(it.name) },
            target
        )
    }
}
