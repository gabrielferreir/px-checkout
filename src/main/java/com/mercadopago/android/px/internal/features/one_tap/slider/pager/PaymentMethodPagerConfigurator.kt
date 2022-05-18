package com.mercadopago.android.px.internal.features.one_tap.slider.pager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mercadopago.android.px.internal.features.one_tap.slider.PaymentMethodPageTransformer

internal object PaymentMethodPagerConfigurator {

    fun configure(pager: ViewPager2, itemPadding: Int) {
        pager.offscreenPageLimit = 1
        pager.setPageTransformer(PaymentMethodPageTransformer(itemPadding))
        pager.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = itemPadding
                outRect.right = itemPadding
            }
        })
        (pager.getChildAt(0) as? RecyclerView)?.let {
            it.clipChildren = false
            it.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }
}
