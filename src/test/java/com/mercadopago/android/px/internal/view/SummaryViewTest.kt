package com.mercadopago.android.px.internal.view

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.mercadopago.android.px.*
import com.mercadopago.android.px.internal.base.PXActivity
import com.mercadopago.android.px.internal.viewmodel.GenericLocalized
import com.mercadopago.android.px.internal.viewmodel.SummaryRowTextDescriptor
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SummaryViewTest : BasicRobolectricTest() {

    @Mock
    private lateinit var logoClickListener: View.OnClickListener

    @Mock
    private lateinit var measureListener: SummaryView.OnMeasureListener

    private lateinit var summaryView: SummaryView

    @Before
    fun setUp() {
        summaryView = SummaryView(getContext())
        with(summaryView) {
            setOnLogoClickListener(logoClickListener)
            setMeasureListener(measureListener)
            setMaxElementsToShow(0)

            val activity = mock(PXActivity::class.java)
            `when`(activity.supportActionBar).thenReturn(mock(ActionBar::class.java))
            configureToolbar(activity, mock(View.OnClickListener::class.java))
            val toolbarModel = ElementDescriptorView.Model("toolbarTitle", null, null, R.drawable.px_icon_default)
            showHorizontalElementDescriptor(toolbarModel)
        }
    }

    @Test
    fun whenInitWithTotalAmountThenInfoIsCorrect() {
        val descriptor = "Total"
        val amount = "$ 100"
        val amountModel = AmountDescriptorView.Model(
            AmountDescriptorView.Model.LabelDescriptor(
                listOf(SummaryRowTextDescriptor(GenericLocalized(descriptor, 0)))
            ),
            SummaryRowTextDescriptor(GenericLocalized(amount, 0))
        )

        summaryView.update(SummaryView.Model(null, emptyList(), amountModel))

        summaryView.getField<AmountDescriptorView>("totalAmountDescriptor").apply {
            getField<TextView>("label").assertText(descriptor)
            getField<TextView>("amount").assertText(amount)
        }
    }

    @Test
    fun whenInitWithTotalAmountMakeHeaderOverlapThenCorrectViewsAreVisible() {
        val descriptor = "Total"
        val amount = "$ 100"
        val amountModel = AmountDescriptorView.Model(
            AmountDescriptorView.Model.LabelDescriptor(
                listOf(SummaryRowTextDescriptor(GenericLocalized(descriptor, 0)))
            ),
            SummaryRowTextDescriptor(GenericLocalized(amount, 0))
        )
        val headerModel = ElementDescriptorView.Model("headerTitle", null, null, R.drawable.px_icon_default)

        with(summaryView) {
            update(SummaryView.Model(headerModel, emptyList(), amountModel))
            animateEnter(500)
            getField<View>("detailRecyclerView").top = 1
            onLayout(true, 0, 0, 0, 0)
            getField<View>("detailRecyclerView").top = 0
            onLayout(true, 0, 0, 0, 0)
            animateExit(500)
        }

        with(summaryView.summaryHeaderCoordinator) {
            getField<View>("horizontalHeaderDescriptor").assertVisible()
            getField<View>("verticalHeaderDescriptor").assertVisible()
        }
    }
}
