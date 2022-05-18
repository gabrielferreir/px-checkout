package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.experiments.ScrolledVariant
import com.mercadopago.android.px.internal.experiments.Variant
import com.mercadopago.android.px.internal.experiments.VariantHandler
import com.mercadopago.android.px.internal.features.one_tap.installments.InstallmentRowHolder
import com.mercadopago.android.px.internal.util.BenefitsHelper
import com.mercadopago.android.px.model.BenefitsMetadata
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.model.PayerCost

internal class InstallmentViewModelMapper(private val currency: Currency, private val benefits: BenefitsMetadata?,
    private val variants: List<Variant>) : Mapper<PayerCost, InstallmentRowHolder.Model>() {

    override fun map(value: PayerCost): InstallmentRowHolder.Model {
        val installments = value.installments
        val interestFreeText = BenefitsHelper.getInterestFreeText(benefits, installments)
        val reimbursementText = BenefitsHelper.getReimbursementText(benefits, installments)
        return InstallmentRowHolder.Model(value, currency, interestFreeText,
            reimbursementText, shouldShowBigRow(value))
    }

    private fun shouldShowBigRow(value: PayerCost): Boolean {
        var showBigRow = benefits != null && benefits.reimbursement != null
        for (variant in variants) {
            variant.process(object : VariantHandler {
                override fun visit(variant: ScrolledVariant) {
                    if (!variant.isDefault()) {
                        showBigRow = showBigRow || value.interestRate != null
                    }
                }
            })
        }
        return showBigRow
    }
}
