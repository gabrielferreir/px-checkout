package com.mercadopago.android.px.internal.features

import android.view.View
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.experiments.KnownVariant
import com.mercadopago.android.px.internal.features.SummaryRowTextDescriptorFactory.AmountType
import com.mercadopago.android.px.internal.features.SummaryRowTextDescriptorFactory.LabelType
import com.mercadopago.android.px.internal.font.PxFont
import com.mercadopago.android.px.internal.repository.CustomTextsRepository
import com.mercadopago.android.px.internal.repository.ExperimentsRepository
import com.mercadopago.android.px.internal.view.AmountDescriptorView
import com.mercadopago.android.px.internal.view.experiments.ExperimentHelper
import com.mercadopago.android.px.internal.viewmodel.GenericColor
import com.mercadopago.android.px.internal.viewmodel.GenericDrawable
import com.mercadopago.android.px.internal.viewmodel.SummaryRowIconDescriptor
import com.mercadopago.android.px.model.DiscountOverview
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import com.mercadopago.android.px.model.internal.SummaryInfo
import java.math.BigDecimal

internal class AmountDescriptorViewModelFactory(
    private val summaryRowTextDescriptorFactory: SummaryRowTextDescriptorFactory,
    val experimentsRepository: ExperimentsRepository? = null
) {

    fun create(customTextsRepository: CustomTextsRepository, amount: BigDecimal): AmountDescriptorView.Model {
        return create(
            customTextsRepository.customTexts.totalDescription,
            LabelType.TOTAL_TEXT,
            amount,
            AmountType.TOTAL_AMOUNT
        )
    }

    fun create(summaryInfo: SummaryInfo, amount: BigDecimal): AmountDescriptorView.Model {
        return create(
            summaryInfo.purpose,
            LabelType.PURPOSE_TEXT,
            amount,
            AmountType.PURPOSE_AMOUNT
        )
    }

    fun create(
        chargeRule: PaymentTypeChargeRule,
        listener: AmountDescriptorView.OnClickListener
    ): AmountDescriptorView.Model {
        return create(
            chargeRule.label,
            LabelType.CHARGE_TEXT,
            chargeRule.charge(),
            AmountType.CHARGE_AMOUNT,
            chargeRule.detailModal?.let { modal ->
                SummaryRowIconDescriptor(
                    GenericDrawable(R.drawable.px_helper),
                    GenericColor(R.color.px_checkout_helper_icon),
                    View.OnClickListener { listener.onChargesAmountDescriptorClicked(modal) }
                )
            }
        )
    }

    fun create(
        model: DiscountOverview,
        hasSplit: Boolean,
        listener: View.OnClickListener
    ): AmountDescriptorView.Model {
        val labelTexts = model.description.map {
            summaryRowTextDescriptorFactory.create(LabelType.DISCOUNT_TEXT, it.message, PxFont.from(it.weight))
        }
        val labelBriefTexts = model.brief.takeIf {
            ExperimentHelper.getVariantFrom(experimentsRepository?.experiments, KnownVariant.SCROLLED).isDefault()
        }?.map {
            summaryRowTextDescriptorFactory.create(LabelType.DISCOUNT_BRIEF_TEXT, it.message, PxFont.from(it.weight))
        }
        val detailIcon = SummaryRowIconDescriptor(
            GenericDrawable(R.drawable.px_helper),
            GenericColor(R.color.px_checkout_helper_icon),
            listener
        )
        val labelDescriptor = AmountDescriptorView.Model.LabelDescriptor(
            labelTexts,
            detailIcon,
            labelBriefTexts,
            !hasSplit
        )
        val amountDescriptor = summaryRowTextDescriptorFactory.create(
            LabelType.DISCOUNT_AMOUNT,
            model.amount.message,
            PxFont.from(model.amount.weight)
        )
        return AmountDescriptorView.Model(labelDescriptor, amountDescriptor)
    }

    private fun create(
        text: String?,
        labelType: LabelType,
        amount: BigDecimal,
        amountType: AmountType,
        detailIcon: SummaryRowIconDescriptor? = null
    ): AmountDescriptorView.Model {
        val labelTexts = listOf(summaryRowTextDescriptorFactory.create(labelType, text))
        val labelDescriptor = AmountDescriptorView.Model.LabelDescriptor(labelTexts, detailIcon)
        val amountDescriptor = summaryRowTextDescriptorFactory.create(amountType, amount)
        return AmountDescriptorView.Model(labelDescriptor, amountDescriptor)
    }
}