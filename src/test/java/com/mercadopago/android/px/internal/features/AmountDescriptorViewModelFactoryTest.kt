package com.mercadopago.android.px.internal.features

import android.view.View
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.R
import com.mercadopago.android.px.addons.model.internal.Experiment
import com.mercadopago.android.px.addons.model.internal.Variant
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.core.DynamicDialogCreator
import com.mercadopago.android.px.internal.font.PxFont
import com.mercadopago.android.px.internal.repository.CustomTextsRepository
import com.mercadopago.android.px.internal.repository.ExperimentsRepository
import com.mercadopago.android.px.internal.view.AmountDescriptorView
import com.mercadopago.android.px.internal.viewmodel.GenericColor
import com.mercadopago.android.px.internal.viewmodel.GenericDrawable
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
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
class AmountDescriptorViewModelFactoryTest : BasicRobolectricTest() {

    private lateinit var currency: Currency
    private lateinit var summaryInfo: SummaryInfo
    private lateinit var customTextsRepository: CustomTextsRepository
    private lateinit var customTexts: CustomTexts
    private lateinit var chargeRule: PaymentTypeChargeRule
    private lateinit var amountDescriptorViewClickListener: AmountDescriptorView.OnClickListener
    private lateinit var experimentsRepository: ExperimentsRepository

    @Before
    fun setUp() {
        // We need this because @Mock does not work with robolectric
        currency = mock(Currency::class.java)
        summaryInfo = mock(SummaryInfo::class.java)
        customTextsRepository = mock(CustomTextsRepository::class.java)
        customTexts = mock(CustomTexts::class.java)
        chargeRule = mock(PaymentTypeChargeRule::class.java)
        amountDescriptorViewClickListener = mock(AmountDescriptorView.OnClickListener::class.java)
        experimentsRepository = mock(ExperimentsRepository::class.java)
        whenever(currency.symbol).thenReturn("$")
    }

    @Test
    fun createFromSummaryInfoShouldReturnPurposeAsLabelAndTotalAsAmountAndNoClickableIcon() {
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
        whenever(summaryInfo.purpose).thenReturn("Test 2")
        val descriptorViewModel = factory.create(summaryInfo, BigDecimal.TEN)
        with(descriptorViewModel.label) {
            this.textDescriptor.size.assertEquals(1)
            this.textDescriptor.first().text.get(getContext()).assertEquals("Test 2")
            assertNull(this.iconDescriptor)
        }
        descriptorViewModel.amount.text.get(getContext()).toString().assertEquals("$ ${BigDecimal.TEN}")
    }

    @Test
    fun createFromSummaryInfoWithoutPurposeShouldReturnDefaultTextAsLabel() {
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
        whenever(summaryInfo.purpose).thenReturn(null)
        val descriptorViewModel = factory.create(summaryInfo, BigDecimal.TEN)
        val defaultText = getContext().getString(R.string.px_summary_detail_item_description)
        with(descriptorViewModel.label.textDescriptor) {
            this.size.assertEquals(1)
            this.first().text.get(getContext()).assertEquals(defaultText)
        }
    }

    @Test
    fun createFromSummaryInfoShouldHavePurposeTextWithPurposeStyle() {
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
        whenever(summaryInfo.purpose).thenReturn("Purpose")
        val descriptorViewModel = factory.create(summaryInfo, BigDecimal.TEN)
        descriptorViewModel.label.textDescriptor.forEach {
            it.font.assertEquals(PxFont.REGULAR)
            it.textSize.assertEquals(R.dimen.px_s_text)
            it.textColor.assertEquals(R.color.px_expressCheckoutTextColor)
        }
        with(descriptorViewModel.amount) {
            this.font.assertEquals(PxFont.REGULAR)
            this.textSize.assertEquals(R.dimen.px_s_text)
            this.textColor.assertEquals(R.color.px_expressCheckoutTextColor)
        }
    }

    @Test
    fun createTotalRowShouldReturnDefaultTotalTextAsLabelAndTotalAsAmountAndNoClickableIcon() {
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
        whenever(customTexts.totalDescription).thenReturn(null)
        whenever(customTextsRepository.customTexts).thenReturn(customTexts)
        val descriptorViewModel = factory.create(customTextsRepository, BigDecimal.TEN)
        val defaultText = getContext().getString(R.string.px_total_to_pay)
        with(descriptorViewModel.label) {
            this.textDescriptor.size.assertEquals(1)
            this.textDescriptor.first().text.get(getContext()).assertEquals(defaultText)
            assertNull(this.iconDescriptor)
        }
        descriptorViewModel.amount.text.get(getContext()).toString().assertEquals("$ ${BigDecimal.TEN}")
    }

    @Test
    fun createTotalRowWithCustomTextShouldReturnCustomTotalTextAsLabel() {
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
        whenever(customTexts.totalDescription).thenReturn("Total custom")
        whenever(customTextsRepository.customTexts).thenReturn(customTexts)
        val descriptorViewModel = factory.create(customTextsRepository, BigDecimal.TEN)
        descriptorViewModel.label.textDescriptor.first().text.get(getContext()).assertEquals("Total custom")
    }

    @Test
    fun createTotalRowShouldHaveTotalRowStyle() {
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
        whenever(customTexts.totalDescription).thenReturn(null)
        whenever(customTextsRepository.customTexts).thenReturn(customTexts)
        val descriptorViewModel = factory.create(customTextsRepository, BigDecimal.TEN)
        descriptorViewModel.label.textDescriptor.forEach {
            it.font.assertEquals(PxFont.SEMI_BOLD)
            it.textSize.assertEquals(R.dimen.px_m_text)
            it.textColor.assertEquals(R.color.px_expressCheckoutTextColor)
        }
        with(descriptorViewModel.amount) {
            this.font.assertEquals(PxFont.SEMI_BOLD)
            this.textSize.assertEquals(R.dimen.px_m_text)
            this.textColor.assertEquals(R.color.px_expressCheckoutTextColor)
        }
    }

    @Test
    fun createFromChargeRuleWithoutDetailModalShouldReturnChargeLabelAsLabelAndChargeAmountAsAmountAndNoClickableIcon() {
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
        whenever(chargeRule.hasDetailModal()).thenReturn(false)
        whenever(chargeRule.label).thenReturn("Label cargo")
        whenever(chargeRule.charge()).thenReturn(BigDecimal.TEN)
        val descriptorViewModel = factory.create(chargeRule, amountDescriptorViewClickListener)
        with(descriptorViewModel.label) {
            this.textDescriptor.size.assertEquals(1)
            this.textDescriptor.first().text.get(getContext()).assertEquals("Label cargo")
            assertNull(this.iconDescriptor)
        }
        descriptorViewModel.amount.text.get(getContext()).toString().assertEquals("$ ${BigDecimal.TEN}")
    }

    @Test
    fun createFromChargeRuleWithNoLabelShouldReturnDefaultChargeLabelAsLabel() {
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
        whenever(chargeRule.hasDetailModal()).thenReturn(false)
        whenever(chargeRule.label).thenReturn(null)
        whenever(chargeRule.charge()).thenReturn(BigDecimal.TEN)
        val descriptorViewModel = factory.create(chargeRule, amountDescriptorViewClickListener)
        val defaultText = getContext().getString(R.string.px_review_summary_charges)
        descriptorViewModel.label.textDescriptor.first().text.get(getContext()).assertEquals(defaultText)
        descriptorViewModel.amount.text.get(getContext()).toString().assertEquals("$ ${BigDecimal.TEN}")
    }

    @Test
    fun createFromChargeRuleWithDetailModalShouldReturnClickableIcon() {
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
        whenever(chargeRule.detailModal).thenReturn(mock(DynamicDialogCreator::class.java))
        whenever(chargeRule.charge()).thenReturn(BigDecimal.TEN)
        val descriptorViewModel = factory.create(chargeRule, amountDescriptorViewClickListener)
        assertNotNull(descriptorViewModel.label.iconDescriptor)
        with(descriptorViewModel.label.iconDescriptor!!) {
            assertTrue(this.drawable is GenericDrawable)
            with(this.drawable as GenericDrawable) {
                this.drawableId.assertEquals(R.drawable.px_helper)
            }
            assertTrue(this.drawableColor is GenericColor)
            with(this.drawableColor as GenericColor) {
                this.colorId.assertEquals(R.color.px_checkout_helper_icon)
            }
        }
        descriptorViewModel.amount.text.get(getContext()).toString().assertEquals("$ ${BigDecimal.TEN}")
    }

    @Test
    fun createFromChargeRuleShouldHaveChargeStyle() {
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency))
        whenever(chargeRule.detailModal).thenReturn(mock(DynamicDialogCreator::class.java))
        whenever(chargeRule.charge()).thenReturn(BigDecimal.TEN)
        val descriptorViewModel = factory.create(chargeRule, amountDescriptorViewClickListener)
        descriptorViewModel.label.textDescriptor.forEach {
            it.textSize.assertEquals(R.dimen.px_xs_text)
            it.font.assertEquals(PxFont.REGULAR)
            it.textColor.assertEquals(R.color.px_expressCheckoutTextColorDiscount)
        }
        with(descriptorViewModel.amount) {
            this.font.assertEquals(PxFont.REGULAR)
            this.textSize.assertEquals(R.dimen.px_xs_text)
            this.textColor.assertEquals(R.color.px_expressCheckoutTextColorDiscount)
        }
        with(descriptorViewModel.label.iconDescriptor!!) {
            (this.drawable as GenericDrawable).drawableId.assertEquals(R.drawable.px_helper)
            (this.drawableColor as GenericColor).colorId.assertEquals(R.color.px_checkout_helper_icon)
        }
    }

    @Test
    fun createFromDiscountWithOneTextAndNoBriefShouldReturnThatTextAsLabelAndTotalAsAmountWithDefaultDescriptor() {
        val labelText = getText("Descuento")
        val amountText = getText("- $ 120")
        whenever(experimentsRepository.experiments).thenReturn(null)
        val discountOverview = DiscountOverview(
            listOf(labelText),
            amountText,
            null,
            null
        )
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency), experimentsRepository)
        val descriptorViewModel = factory.create(discountOverview, false, View.OnClickListener { })

        with(descriptorViewModel.label) {
            this.textDescriptor.size.assertEquals(1)
            this.textDescriptor.first().text.get(getContext()).assertEquals(labelText.message)
            assertNull(this.briefTextDescriptor)
            assertNotNull(this.iconDescriptor)
        }
        descriptorViewModel.amount.text.get(getContext()).toString().assertEquals(amountText.message)
    }

    @Test
    fun createFromDiscountWithMoreThanOneTextShouldContainAllTexts() {
        val labelText = getText("Descuento")
        val otherLabelText = getText("70% OFF")
        val amountText = getText("- $ 120")
        whenever(experimentsRepository.experiments).thenReturn(null)
        val discountOverview = DiscountOverview(
            listOf(labelText, otherLabelText),
            amountText,
            null,
            null
        )
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency), experimentsRepository)
        val descriptorViewModel = factory.create(discountOverview, false, View.OnClickListener { })

        with(descriptorViewModel.label.textDescriptor) {
            this.size.assertEquals(2)
            this.first().text.get(getContext()).assertEquals(labelText.message)
            this[1].text.get(getContext()).assertEquals(otherLabelText.message)
        }
        descriptorViewModel.amount.text.get(getContext()).toString().assertEquals(amountText.message)
    }

    @Test
    fun createFromDiscountWithBriefTextsShouldContainBriefTexts() {
        val labelText = getText("Descuento")
        val briefText = getText("Limite de 100")
        val otherBriefText = getText("por día!")
        val amountText = getText("- $ 120")
        whenever(experimentsRepository.experiments).thenReturn(null)
        val discountOverview = DiscountOverview(
            listOf(labelText),
            amountText,
            listOf(briefText, otherBriefText),
            null
        )
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency), experimentsRepository)
        val descriptorViewModel = factory.create(discountOverview, false, View.OnClickListener { })

        assertNotNull(descriptorViewModel.label.briefTextDescriptor)
        with(descriptorViewModel.label.briefTextDescriptor!!) {
            this.size.assertEquals(2)
            this.first().text.get(getContext()).assertEquals(briefText.message)
            this[1].text.get(getContext()).assertEquals(otherBriefText.message)
        }
    }

    @Test
    fun createFromDiscountShouldNotShowBriefTextsWhenVariantIsScrolled() {
        val labelText = getText("Descuento")
        val briefText = getText("Limite de 100")
        val otherBriefText = getText("por día!")
        val amountText = getText("- $ 120")
        whenever(experimentsRepository.experiments).thenReturn(listOf(
            Experiment(1, "px_nativo/highlight_installments", Variant(1, "scrolled_installments", null)))
        )
        val discountOverview = DiscountOverview(
            listOf(labelText),
            amountText,
            listOf(briefText, otherBriefText),
            null
        )
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency), experimentsRepository)
        val descriptorViewModel = factory.create(discountOverview, false, View.OnClickListener { })
        assertNull(descriptorViewModel.label.briefTextDescriptor)
    }

    @Test
    fun createFromDiscountWithoutUrlShouldHaveDrawableIcon() {
        val labelText = getText("Descuento")
        val amountText = getText("- $ 120")
        whenever(experimentsRepository.experiments).thenReturn(null)
        val discountOverview = DiscountOverview(
            listOf(labelText),
            amountText,
            null,
            null
        )
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency), experimentsRepository)
        val descriptorViewModel = factory.create(discountOverview, false, View.OnClickListener { })
        with(descriptorViewModel.label.iconDescriptor!!) {
            assertTrue(this.drawable is GenericDrawable)
            with(this.drawable as GenericDrawable) {
                this.drawableId.assertEquals(R.drawable.px_helper)
            }
            assertTrue(this.drawableColor is GenericColor)
            with(this.drawableColor as GenericColor) {
                this.colorId.assertEquals(R.color.px_checkout_helper_icon)
            }
        }
    }

    @Test
    fun createFromDiscountShouldHaveDiscountStyle() {
        val labelText = getText("Descuento")
        val briefText = getText("Limite de 100")
        val otherBriefText = getText("por día!")
        val amountText = getText("- $ 120")
        whenever(experimentsRepository.experiments).thenReturn(null)
        val discountOverview = DiscountOverview(
            listOf(labelText),
            amountText,
            listOf(briefText, otherBriefText),
            null
        )
        val factory = AmountDescriptorViewModelFactory(SummaryRowTextDescriptorFactory(currency), experimentsRepository)
        val descriptorViewModel = factory.create(discountOverview, false, View.OnClickListener { })
        descriptorViewModel.label.textDescriptor.forEach {
            it.textSize.assertEquals(R.dimen.px_xs_text)
            it.font.assertEquals(PxFont.REGULAR)
            it.textColor.assertEquals(R.color.px_expressCheckoutTextColorDiscount)
        }
        descriptorViewModel.label.briefTextDescriptor!!.forEach {
            it.textSize.assertEquals(R.dimen.px_xxs_text)
            it.font.assertEquals(PxFont.REGULAR)
            it.textColor.assertEquals(R.color.px_expressCheckoutTextColorDiscount)
        }
        with(descriptorViewModel.amount) {
            this.font.assertEquals(PxFont.REGULAR)
            this.textSize.assertEquals(R.dimen.px_xs_text)
            this.textColor.assertEquals(R.color.px_checkout_discount_amount)
        }
    }
}
