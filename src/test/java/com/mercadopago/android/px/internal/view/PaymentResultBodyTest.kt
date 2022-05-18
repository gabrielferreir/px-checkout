package com.mercadopago.android.px.internal.view

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.drawee.backends.pipeline.Fresco
import com.mercadolibre.android.mlbusinesscomponents.common.MLBusinessSingleItem
import com.mercadolibre.android.mlbusinesscomponents.components.actioncard.MLBusinessActionCardViewData
import com.mercadolibre.android.mlbusinesscomponents.components.common.downloadapp.MLBusinessDownloadAppData
import com.mercadolibre.android.mlbusinesscomponents.components.common.downloadapp.MLBusinessDownloadAppView
import com.mercadolibre.android.mlbusinesscomponents.components.crossselling.MLBusinessCrossSellingBoxData
import com.mercadolibre.android.mlbusinesscomponents.components.crossselling.MLBusinessCrossSellingBoxView
import com.mercadolibre.android.mlbusinesscomponents.components.discount.MLBusinessDiscountBoxData
import com.mercadolibre.android.mlbusinesscomponents.components.loyalty.MLBusinessLoyaltyRingData
import com.mercadolibre.android.mlbusinesscomponents.components.touchpoint.domain.response.MLBusinessTouchpointResponse
import com.mercadopago.android.px.*
import com.mercadopago.android.px.internal.features.business_result.CongratsViewModel
import com.mercadopago.android.px.internal.features.business_result.PXDiscountBoxData
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsResponse
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PaymentResultBodyTest : BasicRobolectricTest() {

    @Mock
    private lateinit var congratsViewModel: CongratsViewModel

    @Mock
    private lateinit var listener: PaymentResultBody.Listener

    @Mock
    private lateinit var mercadoPagoAppInfo: ApplicationInfo

    private lateinit var modelBuilder: PaymentResultBody.Model.Builder

    private lateinit var body: PaymentResultBody

    @Before
    fun setUp() {
        val packageManager = mock(PackageManager::class.java)
        val context = spy(getContext())
        `when`(packageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(mercadoPagoAppInfo)
        `when`(context.packageManager).thenReturn(packageManager)

        if (!Fresco.hasBeenInitialized()) {
            Fresco.initialize(context)
        }

        body = PaymentResultBody(context)
        modelBuilder = PaymentResultBody.Model.Builder().setCongratsViewModel(congratsViewModel)
    }

    @Test
    fun whenInitWithEmptyModelThenViewsAreGone() {
        body.init(modelBuilder.build(), mock(PaymentResultBody.Listener::class.java))

        with(body) {
            assertGone(R.id.loyaltyView)
            assertGone(R.id.dividingLineView)
            assertGone(R.id.showAllDiscounts)
            assertGone(R.id.money_split_view)
            assertGone(R.id.downloadView)
            assertGone(R.id.receipt)
            assertGone(R.id.help)
            assertGone(R.id.primaryMethod)
            assertGone(R.id.secondaryMethod)
            assertGone(R.id.view_receipt_action)
            assertGone(R.id.px_fragment_container_important)
            assertGone(R.id.px_fragment_container_top)
            assertGone(R.id.px_fragment_container_bottom)
            assertGone(R.id.operationInfo)
        }
    }

    @Test
    fun whenInitWithCrossSellingDataThenViewsAreAddedToParent() {
        val crossSellingBoxData = mock(MLBusinessCrossSellingBoxData::class.java)
        `when`(congratsViewModel.crossSellingBoxData).thenReturn(listOf(crossSellingBoxData))

        body.init(modelBuilder.build(), listener)

        body.findView<LinearLayout>(R.id.businessComponents).assertChildCount<MLBusinessCrossSellingBoxView>(1)
    }

    @Test
    fun whenInitWithReceiptIdAndHelpThenViewsAreVisible() {
        modelBuilder.setReceiptId("receiptId")
        modelBuilder.setHelp("help")

        body.init(modelBuilder.build(), listener)

        with(body) {
            assertVisible(R.id.receipt)
            assertVisible(R.id.help)
        }
    }

    @Test
    fun whenInitWithActionCardDataThenViewIsVisibleAndClickCallsListener() {
        val actionCardViewData = mock(MLBusinessActionCardViewData::class.java)
        `when`(actionCardViewData.getTitleBackgroundColor()).thenReturn("#000000")
        `when`(actionCardViewData.getTitleColor()).thenReturn("#ffffff")
        `when`(actionCardViewData.getTitleWeight()).thenReturn("bold")
        `when`(congratsViewModel.actionCardViewData).thenReturn(actionCardViewData)

        body.init(modelBuilder.build(), listener)

        body.findView<View>(R.id.money_split_view).apply {
            performClick()
            assertVisible()
        }
        verify(listener).onClickMoneySplit()
    }

    @Test
    fun whenInitWithLoyaltyDataThenViewIsVisible() {
        val loyaltyRingData = mock(MLBusinessLoyaltyRingData::class.java)
        `when`(loyaltyRingData.ringHexaColor).thenReturn("#ffffff")
        `when`(congratsViewModel.loyaltyRingData).thenReturn(loyaltyRingData)

        body.init(modelBuilder.build(), listener)

        body.assertVisible(R.id.loyaltyView)
    }

    @Test
    fun whenInitWithDiscountBoxDataThenViewIsVisibleAndClickCallsListener() {
        val discountBoxData = mock(MLBusinessDiscountBoxData::class.java)
        val discountBoxItem = mock(MLBusinessSingleItem::class.java)
        `when`(discountBoxItem.imageUrl).thenReturn("https://example.com/image.png")
        `when`(discountBoxData.items).thenReturn(listOf(discountBoxItem, discountBoxItem))
        val pxDiscountBoxData = mock(PXDiscountBoxData::class.java)
        `when`(pxDiscountBoxData.discountBoxData).thenReturn(discountBoxData)
        `when`(congratsViewModel.discountBoxData).thenReturn(pxDiscountBoxData)
        val target = "target"
        mercadoPagoAppInfo.enabled = true
        `when`(congratsViewModel.showAllDiscounts).thenReturn(PaymentCongratsResponse.Action("label", target))

        body.init(modelBuilder.build(), listener)

        with(body) {
            assertVisible(R.id.discountView)
            findView<View>(R.id.showAllDiscounts).apply {
                performClick()
                assertVisible()
            }
        }
        verify(listener).onClickShowAllDiscounts(target)
    }

    @Test
    fun whenInitWithMethodsDataThenViewsAreVisible() {
        val amountModel = PaymentResultAmount.Model.Builder("10", "10").build()
        val methodModel = PaymentResultMethod.Model.Builder(amountModel, listOf(
            PaymentCongratsText("1234"), PaymentCongratsText("123456"))
        ).build()

        modelBuilder.setPaymentResultMethodModels(listOf(methodModel, methodModel))

        body.init(modelBuilder.build(), listener)

        with(body) {
            assertVisible(R.id.primaryMethod)
            assertVisible(R.id.secondaryMethod)
        }
    }

    @Test
    fun whenInitWithTouchPointDataThenViewIsVisible() {
        val touchPointData = mock(MLBusinessTouchpointResponse::class.java)
        touchPointData.id = "id"
        touchPointData.type = "type"
        val title = "title"
        val pxDiscountBoxData = mock(PXDiscountBoxData::class.java)
        `when`(pxDiscountBoxData.title).thenReturn(title)
        `when`(pxDiscountBoxData.touchpoint).thenReturn(touchPointData)
        val downloadAppData = mock(MLBusinessDownloadAppData::class.java)
        `when`(downloadAppData.appSite).thenReturn(MLBusinessDownloadAppView.AppSite.MP)
        `when`(congratsViewModel.discountBoxData).thenReturn(pxDiscountBoxData)
        `when`(congratsViewModel.downloadAppData).thenReturn(downloadAppData)

        body.init(modelBuilder.build(), listener)

        with(body) {
            assertVisible(R.id.touchpointView)
            findView<TextView>(R.id.touchpointLabelView).apply {
                assertText(title)
                assertVisible()
            }
            assertVisible(R.id.downloadView)
        }
    }

    @Test
    fun whenInitWithViewReceiptModelThenViewIsVisibleAndClickCallsListener() {
        val target = "target"
        val viewReceiptModel = PaymentCongratsResponse.Action("label", target)
        `when`(congratsViewModel.viewReceipt).thenReturn(viewReceiptModel)
        mercadoPagoAppInfo.enabled = true

        body.init(modelBuilder.build(), listener)

        body.findView<View>(R.id.view_receipt_action).apply {
            performClick()
            assertVisible()
        }
        verify(listener).onClickViewReceipt(target)
    }
}
