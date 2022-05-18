package com.mercadopago.android.px.internal.view

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.ImageView
import com.mercadopago.android.px.*
import com.mercadopago.android.px.core.DynamicDialogCreator
import com.mercadopago.android.px.internal.features.AmountDescriptorViewModelFactory
import com.mercadopago.android.px.internal.features.SummaryRowTextDescriptorFactory
import com.mercadopago.android.px.internal.repository.CustomTextsRepository
import com.mercadopago.android.px.internal.repository.ExperimentsRepository
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.model.DiscountOverview
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import com.mercadopago.android.px.model.internal.CustomTexts
import com.mercadopago.android.px.model.internal.SummaryInfo
import com.mercadopago.android.px.utils.TextUtils.getText
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
class AmountDescriptorViewTest : BasicRobolectricTest() {

    private lateinit var currency: Currency
    private lateinit var summaryInfo: SummaryInfo
    private lateinit var customTextsRepository: CustomTextsRepository
    private lateinit var customTexts: CustomTexts
    private lateinit var chargeRule: PaymentTypeChargeRule
    private lateinit var amountDescriptorViewClickListener: AmountDescriptorView.OnClickListener
    private lateinit var experimentsRepository: ExperimentsRepository

    private var amountDescriptorView: AmountDescriptorView = AmountDescriptorView(getContext())

    @Before
    fun setUp() {
        // We need this because @Mock does not work with robolectric
        currency = Mockito.mock(Currency::class.java)
        summaryInfo = Mockito.mock(SummaryInfo::class.java)
        customTextsRepository = Mockito.mock(CustomTextsRepository::class.java)
        customTexts = Mockito.mock(CustomTexts::class.java)
        chargeRule = Mockito.mock(PaymentTypeChargeRule::class.java)
        amountDescriptorViewClickListener = Mockito.mock(AmountDescriptorView.OnClickListener::class.java)
        experimentsRepository = Mockito.mock(ExperimentsRepository::class.java)
        whenever(currency.symbol).thenReturn("$")
    }

    @Test
    fun updateTotalRowModelShouldHaveLabelAndAmountVisiblesAndBriefAndIconShouldBeGone() {
        whenever(customTexts.totalDescription).thenReturn(null)
        whenever(customTextsRepository.customTexts).thenReturn(customTexts)
        val descriptorModel = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
            .create(customTextsRepository, BigDecimal.TEN)
        amountDescriptorView.update(descriptorModel)
        with(amountDescriptorView) {
            with(getField<MPTextView>("label")) {
                text.toString().assertEquals(context.getString(R.string.px_total_to_pay))
                assertVisible()
            }
            with(getField<MPTextView>("amount")) {
                text.toString().assertEquals("$ 10")
                assertVisible()
            }
            getField<MPTextView>("brief").assertGone()
            getField<ImageView>("labelIcon").assertInvisible()
            assertFalse(hasOnClickListeners())
        }
    }

    @Test
    fun updatePurposeRowModelShouldHaveLabelAndAmountVisiblesAndBriefAndIconShouldBeGone() {
        whenever(customTexts.totalDescription).thenReturn(null)
        whenever(customTextsRepository.customTexts).thenReturn(customTexts)
        whenever(summaryInfo.purpose).thenReturn("Purpose")
        val descriptorModel = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
            .create(summaryInfo, BigDecimal.TEN)
        amountDescriptorView.update(descriptorModel)
        with(amountDescriptorView) {
            with(getField<MPTextView>("label")) {
                text.toString().assertEquals("Purpose")
                assertVisible()
            }
            with(getField<MPTextView>("amount")) {
                text.toString().assertEquals("$ 10")
                assertVisible()
            }
            getField<MPTextView>("brief").assertGone()
            getField<ImageView>("labelIcon").assertInvisible()
            assertFalse(hasOnClickListeners())
        }
    }

    @Test
    fun updateChargeRowModelWithModalShouldHaveLabelAndAmountAndIconVisiblesAndBriefShouldBeGone() {
        whenever(chargeRule.detailModal).thenReturn(Mockito.mock(DynamicDialogCreator::class.java))
        whenever(chargeRule.charge()).thenReturn(BigDecimal.TEN)
        val descriptorModel = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
            .create(chargeRule, amountDescriptorViewClickListener)
        amountDescriptorView.update(descriptorModel)
        with(amountDescriptorView) {
            with(getField<MPTextView>("label")) {
                text.toString().assertEquals(context.getString(R.string.px_review_summary_charges))
                assertVisible()
            }
            with(getField<MPTextView>("amount")) {
                text.toString().assertEquals("$ 10")
                assertVisible()
            }

            getField<MPTextView>("brief").assertGone()
            with (getField<ImageView>("labelIcon")) {
                assertVisible()
                Shadows.shadowOf(drawable).createdFromResId.assertEquals(R.drawable.px_helper)
                colorFilter.assertEquals(
                    PorterDuffColorFilter(context.getColor(R.color.px_checkout_helper_icon), PorterDuff.Mode.SRC_ATOP)
                )
            }
            assertTrue(hasOnClickListeners())
        }
    }

    @Test
    fun updateChargeRowModelWithoutModalShouldSetIconInvisible() {
        whenever(chargeRule.detailModal).thenReturn(null)
        whenever(chargeRule.charge()).thenReturn(BigDecimal.TEN)
        val descriptorModel = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
            .create(chargeRule, amountDescriptorViewClickListener)
        amountDescriptorView.update(descriptorModel)
        with(amountDescriptorView) {
            getField<ImageView>("labelIcon").assertInvisible()
            assertFalse(hasOnClickListeners())
        }
    }

    @Test
    fun updateDiscountRowModelWithoutBriefShouldHaveLabelAndAmountAndIconVisiblesAndBriefShouldBeGone() {
        val labelText = getText("Descuento")
        val amountText = getText("- $ 120")
        whenever(experimentsRepository.experiments).thenReturn(null)
        val discountOverview = DiscountOverview(
            listOf(labelText),
            amountText,
            null,
            "test.url.com"
        )
        val descriptorModel = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
            .create(discountOverview, false, View.OnClickListener { })
        amountDescriptorView.update(descriptorModel)
        with(amountDescriptorView) {
            with(getField<MPTextView>("label")) {
                text.toString().assertEquals(labelText.message)
                assertVisible()
            }
            with(getField<MPTextView>("amount")) {
                text.toString().assertEquals(amountText.message)
                assertVisible()
            }

            getField<MPTextView>("brief").assertGone()
            with(getField<ImageView>("labelIcon")) {
                assertVisible()
            }
            assertTrue(hasOnClickListeners())
        }
    }


    @Test
    fun updateDiscountRowModelWithBriefAndNoSplitShouldHaveBriefVisible() {
        val labelText = getText("Descuento")
        val amountText = getText("- $ 120")
        val briefText = getText("Brief texxt")
        whenever(experimentsRepository.experiments).thenReturn(null)
        val discountOverview = DiscountOverview(
            listOf(labelText),
            amountText,
            listOf(briefText),
            "test.url.com"
        )
        val descriptorModel = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
            .create(discountOverview, false, View.OnClickListener { })
        amountDescriptorView.update(descriptorModel)
        with(amountDescriptorView) {
            with(getField<MPTextView>("brief")) {
                text.toString().assertEquals(briefText.message)
                assertVisible()
            }
        }
    }

    @Test
    fun updateDiscountRowModelWithBriefAndSplitShouldHaveBriefGone() {
        val labelText = getText("Descuento")
        val amountText = getText("- $ 120")
        val briefText = getText("Brief texxt")
        whenever(experimentsRepository.experiments).thenReturn(null)
        val discountOverview = DiscountOverview(
            listOf(labelText),
            amountText,
            listOf(briefText),
            "test.url.com"
        )
        val descriptorModel = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
            .create(discountOverview, true, View.OnClickListener { })
        amountDescriptorView.update(descriptorModel)
        with(amountDescriptorView) {
            with(getField<MPTextView>("brief")) {
                assertGone()
            }
        }
    }
}
