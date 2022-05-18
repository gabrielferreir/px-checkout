package com.mercadopago.android.px.internal.features.one_tap.slider.pager

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback

internal class ViewPagerAttacher {
    private lateinit var adapterDataObserver: RecyclerView.AdapterDataObserver
    private lateinit var onPageChangeCallback: OnPageChangeCallback
    private lateinit var pager: ViewPager2
    private lateinit var attachedAdapter: RecyclerView.Adapter<*>

    fun attachToPager(indicator: ScrollingPagerIndicator, pager: ViewPager2) {
        this.pager = pager
        attachedAdapter = requireNotNull(pager.adapter) { "Set adapter before call attachToPager() method" }

        indicator.setDotCount(attachedAdapter.itemCount)
        indicator.setCurrentPosition(pager.currentItem)
        adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                indicator.reattach()
            }
        }
        attachedAdapter.registerAdapterDataObserver(adapterDataObserver)
        onPageChangeCallback = object : OnPageChangeCallback() {
            private var idleState = true
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                // ViewPager may emit negative positionOffset for very fast scrolling
                val offset = positionOffset.coerceIn(0f, 1f)
                indicator.onPageScrolled(position, offset)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (idleState) {
                    indicator.setDotCount(attachedAdapter.itemCount)
                    indicator.setCurrentPosition(pager.currentItem)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                idleState = state == ViewPager.SCROLL_STATE_IDLE
            }
        }
        pager.registerOnPageChangeCallback(onPageChangeCallback)
    }

    fun detachFromPager() {
        attachedAdapter.unregisterAdapterDataObserver(adapterDataObserver)
        pager.unregisterOnPageChangeCallback(onPageChangeCallback)
    }
}
