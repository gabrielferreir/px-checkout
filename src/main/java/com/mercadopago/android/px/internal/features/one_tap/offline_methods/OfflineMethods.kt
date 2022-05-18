package com.mercadopago.android.px.internal.features.one_tap.offline_methods

import androidx.lifecycle.LiveData
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton
import com.mercadopago.android.px.internal.features.pay_button.PayButton
import com.mercadopago.android.px.internal.viewmodel.AmountLocalized
import com.mercadopago.android.px.model.ExpressMetadata
import com.mercadopago.android.px.model.OfflinePaymentType
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.model.internal.Text

internal interface OfflineMethods {

    interface View : PayButton.Handler {
        fun showExpanded()
        fun showCollapsed()
    }

    interface ViewModel {
        val deepLinkLiveData: LiveData<String>
        fun onSheetShowed()
        fun onMethodSelected(selectedItem: OfflineMethodItem)
        fun onGetViewTrackPath(callback: ConfirmButton.ViewTrackPathCallback)
        fun onPrePayment(callback: ConfirmButton.OnReadyForProcessCallback)
        fun onBack()
        fun onPaymentExecuted(configuration: PaymentConfiguration)
    }

    interface OnMethodSelectedListener {
        fun onItemSelected(selectedItem: OfflineMethodItem)
    }

    data class Model(
        val bottomDescription: Text?,
        val amountLocalized: AmountLocalized,
        val offlinePaymentTypes: List<OfflinePaymentType>)

    companion object {
        @JvmStatic
        fun shouldLaunch(expressMetadataList: List<ExpressMetadata>): Boolean {
            return expressMetadataList.filter { express -> express.status.run { isEnabled } }
                .run { size == 1 && with(get(0)) { isOfflineMethods && !isNewCard } }
        }
    }
}