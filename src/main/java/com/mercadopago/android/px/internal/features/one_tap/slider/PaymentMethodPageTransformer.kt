package com.mercadopago.android.px.internal.features.one_tap.slider

import android.view.View
import androidx.viewpager2.widget.ViewPager2

private const val PAGER_MARGIN_MULTIPLIER = -1.5f

internal class PaymentMethodPageTransformer(private val itemPadding: Int) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.translationX = itemPadding * PAGER_MARGIN_MULTIPLIER * position
    }
}
