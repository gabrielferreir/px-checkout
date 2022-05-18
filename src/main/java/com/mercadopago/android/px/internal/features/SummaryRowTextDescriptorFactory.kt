package com.mercadopago.android.px.internal.features

import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.font.PxFont
import com.mercadopago.android.px.internal.viewmodel.AmountLocalized
import com.mercadopago.android.px.internal.viewmodel.GenericLocalized
import com.mercadopago.android.px.internal.viewmodel.SummaryRowTextDescriptor
import com.mercadopago.android.px.model.Currency
import java.math.BigDecimal

internal class SummaryRowTextDescriptorFactory(val currency: Currency) {

    fun create(labelType: LabelType, text: String?, font: PxFont = PxFont.REGULAR): SummaryRowTextDescriptor {
        return when (labelType) {
            LabelType.PURPOSE_TEXT ->
                SummaryRowTextDescriptor(
                    GenericLocalized(text, R.string.px_summary_detail_item_description),
                    R.color.px_expressCheckoutTextColor,
                    font,
                    R.dimen.px_s_text)
            LabelType.DISCOUNT_TEXT ->
                SummaryRowTextDescriptor(
                    GenericLocalized(text, 0),
                    R.color.px_expressCheckoutTextColorDiscount,
                    font,
                    R.dimen.px_xs_text
                )
            LabelType.DISCOUNT_BRIEF_TEXT ->
                SummaryRowTextDescriptor(
                    GenericLocalized(text, 0),
                    R.color.px_expressCheckoutTextColorDiscount,
                    font,
                    R.dimen.px_xxs_text
                )
            LabelType.DISCOUNT_AMOUNT ->
                SummaryRowTextDescriptor(
                    GenericLocalized(text, 0),
                    R.color.px_checkout_discount_amount,
                    font,
                    R.dimen.px_xs_text
                )
            LabelType.CHARGE_TEXT ->
                SummaryRowTextDescriptor(
                    GenericLocalized(text, R.string.px_review_summary_charges),
                    R.color.px_expressCheckoutTextColorDiscount,
                    font,
                    R.dimen.px_xs_text
                )
            LabelType.TOTAL_TEXT ->
                SummaryRowTextDescriptor(
                    GenericLocalized(text, R.string.px_total_to_pay),
                    R.color.px_expressCheckoutTextColor,
                    PxFont.SEMI_BOLD,
                    R.dimen.px_m_text)
        }
    }

    fun create(amountType: AmountType, amount: BigDecimal, font: PxFont = PxFont.REGULAR): SummaryRowTextDescriptor {
        val amountLocalized = AmountLocalized(amount, currency)
        return when (amountType) {
            AmountType.CHARGE_AMOUNT ->
                SummaryRowTextDescriptor(amountLocalized, R.color.px_expressCheckoutTextColorDiscount, font, R.dimen.px_xs_text)
            AmountType.PURPOSE_AMOUNT ->
                SummaryRowTextDescriptor(amountLocalized, R.color.px_expressCheckoutTextColor, font, R.dimen.px_s_text)
            AmountType.TOTAL_AMOUNT ->
                SummaryRowTextDescriptor(amountLocalized, R.color.px_expressCheckoutTextColor, PxFont.SEMI_BOLD, R.dimen.px_m_text)
        }
    }

    enum class LabelType {
        PURPOSE_TEXT,
        DISCOUNT_TEXT,
        DISCOUNT_BRIEF_TEXT,
        DISCOUNT_AMOUNT,  //This one is here because it comes from backend as a formatted string and not as number
        CHARGE_TEXT,
        TOTAL_TEXT
    }
    enum class AmountType {
        CHARGE_AMOUNT,
        PURPOSE_AMOUNT,
        TOTAL_AMOUNT
    }
}