package com.mercadopago.android.px.internal.view

import android.view.View
import android.widget.FrameLayout
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.features.one_tap.slider.ViewAdapter
import com.mercadopago.android.px.internal.viewmodel.GoingToModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TitlePagerTest : BasicRobolectricTest() {

    private lateinit var titlePager: TitlePager

    @Before
    fun setUp() {
        titlePager = TitlePagerDefault(getContext(), null)
        with(titlePager) {
            addView(mock(View::class.java))
            addView(mock(View::class.java))
            addView(mock(View::class.java))
            onFinishInflate()
            setAdapter(mock(ViewAdapter::class.java))
        }
    }

    @Test
    fun whenUpdatePositionGoingForwardThenViewsAreTranslated() {
        val width = 100
        val offset = 0.5f
        with(titlePager) {
            val layoutParams = FrameLayout.LayoutParams(width, 0)
            this.layoutParams = layoutParams
            `when`(currentView.layoutParams).thenReturn(layoutParams)
            `when`(previousView.layoutParams).thenReturn(layoutParams)
            `when`(nextView.layoutParams).thenReturn(layoutParams)
            measure(0, 0)
            layout(0, 0, width, 0)
            titlePager.onGlobalLayout()
        }

        titlePager.updatePosition(offset, GoingToModel.FORWARD)

        with(titlePager) {
            verify(previousView).x = eq(width * -1.5f)
            verify(currentView).x = eq(width * -0.5f)
            verify(nextView).x = eq(width * 0.5f)
        }
    }

    @Test
    fun whenOrderViewsGoingForwardThenViewsAreOrdered() {
        val expectedNextView = titlePager.currentView
        val expectedPreviousView = titlePager.nextView
        val expectedCurrentView = titlePager.previousView

        titlePager.orderViews(GoingToModel.FORWARD)

        with(titlePager) {
            nextView.assertEquals(expectedNextView)
            currentView.assertEquals(expectedCurrentView)
            previousView.assertEquals(expectedPreviousView)
        }
    }

    @Test
    fun whenUpdatePositionGoingBackwardsThenViewsAreTranslated() {
        val width = 100
        val offset = 0.5f
        with(titlePager) {
            val layoutParams = FrameLayout.LayoutParams(width, 0)
            this.layoutParams = layoutParams
            `when`(currentView.layoutParams).thenReturn(layoutParams)
            `when`(previousView.layoutParams).thenReturn(layoutParams)
            `when`(nextView.layoutParams).thenReturn(layoutParams)
            measure(0, 0)
            layout(0, 0, width, 0)
            titlePager.onGlobalLayout()
        }

        titlePager.updatePosition(offset, GoingToModel.BACKWARDS)

        with(titlePager) {
            verify(previousView).x = eq(width * -0.5f)
            verify(currentView).x = eq(width * 0.5f)
            verify(nextView).x = eq(width * 1.5f)
        }
    }

    @Test
    fun whenOrderViewsGoingBackwardsThenViewsAreOrdered() {
        val expectedNextView = titlePager.previousView
        val expectedPreviousView = titlePager.currentView
        val expectedCurrentView = titlePager.nextView

        titlePager.orderViews(GoingToModel.BACKWARDS)

        with(titlePager) {
            nextView.assertEquals(expectedNextView)
            currentView.assertEquals(expectedCurrentView)
            previousView.assertEquals(expectedPreviousView)
        }
    }
}
