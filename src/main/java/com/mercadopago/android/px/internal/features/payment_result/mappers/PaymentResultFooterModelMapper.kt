package com.mercadopago.android.px.internal.features.payment_result.mappers

import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.extensions.isNotNull
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultButton
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultFooter
import com.mercadopago.android.px.internal.viewmodel.LazyString
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.internal.mappers.PaymentResultButtonMapper
import com.mercadopago.android.px.model.internal.CongratsResponse

internal object PaymentResultFooterModelMapper : Mapper<PaymentModel, PaymentResultFooter.Model?>() {

    override fun map(model: PaymentModel): PaymentResultFooter.Model? {
        val remedies = model.remedies
        val congrats = model.congratsResponse
        val changePmButton = PaymentResultButton(
            PaymentResultButton.Type.QUIET,
            LazyString(R.string.px_change_payment_method),
            PaymentResultButton.Action.CHANGE_PM
        )
        return when {
            remedies.suggestedPaymentMethod.isNotNull() || remedies.cvv.isNotNull() -> PaymentResultFooter.Model(
                null,
                changePmButton,
                showPayButton = true
            )
            remedies.highRisk.isNotNull() -> PaymentResultFooter.Model(
                PaymentResultButton(
                    PaymentResultButton.Type.LOUD,
                    LazyString(remedies.highRisk!!.actionLoud.label),
                    PaymentResultButton.Action.KYC
                ),
                changePmButton
            )
            hasDynamicButtons(congrats) -> PaymentResultFooter.Model(
                PaymentResultButtonMapper.map(congrats.primaryButton),
                PaymentResultButtonMapper.map(congrats.secondaryButton)
            )
            else -> null
        }
    }

    private fun hasDynamicButtons(congrats: CongratsResponse): Boolean {
        return congrats.primaryButton.isNotNull() || congrats.secondaryButton.isNotNull()
    }
}
