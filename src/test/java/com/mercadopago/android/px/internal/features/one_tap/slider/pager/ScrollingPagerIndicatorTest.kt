package com.mercadopago.android.px.internal.features.one_tap.slider.pager

import android.graphics.Canvas
import android.graphics.Color
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.R
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.getField
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScrollingPagerIndicatorTest : BasicRobolectricTest() {

    @Test
    fun initWithParametersAndSetsCurrentPosition() {
        val pager = mock(ViewPager2::class.java)
        `when`(pager.adapter).thenReturn(mock(FragmentStateAdapter::class.java))
        val blackColor = "#000000"
        val whiteColor = "#ffffff"
        val visibleDotCount = 5
        val attr = Robolectric.buildAttributeSet()
            .addAttribute(R.attr.px_spi_dotColor, blackColor)
            .addAttribute(R.attr.px_spi_dotSelectedColor, whiteColor)
            .addAttribute(R.attr.px_spi_visibleDotCount, visibleDotCount.toString())
            .build()

        val indicator = ScrollingPagerIndicator(getContext(), attr)
        with(indicator) {
            attachToPager(pager)
            setDotCount(7)
            onPageScrolled(1, 0.5f)
            setCurrentPosition(2)
            onMeasure(0, 0)
            onDraw(mock(Canvas::class.java))
        }

        with(indicator) {
            getField<Int>("dotColor").assertEquals(Color.parseColor(blackColor))
            getField<Int>("selectedDotColor").assertEquals(Color.parseColor(whiteColor))
            getField<Int>("visibleDotCount").assertEquals(visibleDotCount)
        }
    }
}
