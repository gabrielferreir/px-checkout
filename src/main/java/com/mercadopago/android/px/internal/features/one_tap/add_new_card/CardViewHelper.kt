package com.mercadopago.android.px.internal.features.one_tap.add_new_card

import com.mercadopago.android.px.R
import com.mercadopago.android.px.model.PXBorder.PXBorderType

internal class CardViewHelper {

    fun getDrawableResByBorderType(borderType: PXBorderType) = when (borderType) {
        PXBorderType.DOTTED -> R.drawable.card_view_dotted_border
        PXBorderType.SOLID -> R.drawable.card_view_solid_border
        else -> R.drawable.card_view_borderless
    }
}
