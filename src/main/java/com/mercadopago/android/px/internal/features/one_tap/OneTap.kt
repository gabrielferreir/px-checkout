package com.mercadopago.android.px.internal.features.one_tap

import android.net.Uri
import com.mercadolibre.android.cardform.internal.LifecycleListener
import com.mercadopago.android.px.internal.base.MvpView
import com.mercadopago.android.px.internal.features.one_tap.slider.HubAdapter
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState
import com.mercadopago.android.px.internal.features.one_tap.installments.InstallmentRowHolder
import com.mercadopago.android.px.internal.view.ElementDescriptorView
import com.mercadopago.android.px.model.DiscountConfigurationModel
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod
import com.mercadopago.android.px.model.StatusMetadata
import com.mercadopago.android.px.core.DynamicDialogCreator
import com.mercadopago.android.px.internal.experiments.Variant
import com.mercadopago.android.px.internal.features.generic_modal.GenericDialogItem
import com.mercadopago.android.px.internal.util.CardFormWrapper
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.internal.features.generic_modal.ActionType
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction
import com.mercadopago.android.px.internal.view.SummaryView
import com.mercadopago.android.px.internal.viewmodel.FlowConfigurationModel
import com.mercadopago.android.px.internal.features.pay_button.PaymentState
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.model.PayerCost
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.model.one_tap.CheckoutBehaviour

internal interface OneTap {
    interface View : MvpView {
        fun configurePayButton(listener: ConfirmButton.StateChange)
        fun clearAdapters()
        fun configureRenderMode(variants: List<@JvmSuppressWildcards Variant>)
        fun configureFlow(flowConfigurationModel: FlowConfigurationModel)
        fun configureAdapters(model: HubAdapter.Model)
        fun updatePaymentMethods(items: List<@JvmSuppressWildcards DrawableFragmentItem?>)
        fun cancel()
        fun updateViewForPosition(
            paymentMethodIndex: Int,
            payerCostSelected: Int,
            splitSelectionState: SplitSelectionState,
            application: Application
        )
        fun updateTotalValue(model: SummaryView.Model)
        fun updateInstallmentsList(selectedIndex: Int, models: List<InstallmentRowHolder.Model?>)
        fun animateInstallmentsList()
        fun showHorizontalElementDescriptor(elementDescriptorModel: ElementDescriptorView.Model)
        fun collapseInstallmentsSelection()
        fun showDiscountDetailDialog(currency: Currency, discountModel: DiscountConfigurationModel)
        fun showDisabledPaymentMethodDetailDialog(disabledPaymentMethod: DisabledPaymentMethod, currentStatus: StatusMetadata)
        fun setPagerIndex(index: Int)
        fun showDynamicDialog(creatorFor: DynamicDialogCreator, checkoutData: DynamicDialogCreator.CheckoutData)
        fun showOfflineMethodsExpanded()
        fun showOfflineMethodsCollapsed()
        fun showGenericDialog(item: GenericDialogItem)
        fun startAddNewCardFlow(cardFormWrapper: CardFormWrapper?)
        fun startDeepLink(deepLink: String)
        fun onDeepLinkReceived(uri: Uri)
        fun showLoading()
        fun hideLoading()
        fun configurePaymentMethodHeader(variant: List<@JvmSuppressWildcards Variant>)
        fun showError(mercadoPagoError: MercadoPagoError)
        fun showErrorActivity(mercadoPagoError: MercadoPagoError)
        fun onSecurityCodeRequired(paymentState: PaymentState)
    }

    interface Presenter {
        fun onFreshStart()
        fun cancel()
        fun onBack()
        fun loadViewModel()
        fun onInstallmentsRowPressed()
        fun updateInstallments()
        fun onInstallmentSelectionCanceled()
        fun onSliderOptionSelected(paymentMethodIndex: Int)
        fun onPayerCostSelected(payerCostSelected: PayerCost)
        fun onSplitChanged(isChecked: Boolean)
        fun onHeaderClicked()
        fun onOtherPaymentMethodClicked()
        fun handlePrePaymentAction(callback: ConfirmButton.OnReadyForProcessCallback)
        fun handleBehaviour(@CheckoutBehaviour.Type behaviourType: String): Boolean
        fun handleGenericDialogAction(type: ActionType)
        fun onProcessExecuted(paymentConfiguration: PaymentConfiguration)
        fun handleDeepLink()
        fun onPostPaymentAction(postPaymentAction: PostPaymentAction)
        fun onCardAdded(cardId: String, callback: LifecycleListener.Callback)
        fun onBankAccountAdded(uri: Uri)
        fun onCardAddedResult()
        fun onApplicationChanged(paymentTypeId: String)
        fun onGetViewTrackPath(callback: ConfirmButton.ViewTrackPathCallback)
        fun resolveDeepLink(uri: Uri)
    }

    enum class NavigationState {
        NONE, CARD_FORM, SECURITY_CODE
    }
}
