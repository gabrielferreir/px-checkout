package com.mercadopago.android.px.internal.features.pay_button

import com.mercadopago.android.px.R
import com.mercadopago.android.px.addons.model.SecurityValidationData
import com.mercadopago.android.px.internal.features.explode.ExplodeDecorator
import com.mercadopago.android.px.model.IParcelablePaymentDescriptor
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel as ButtonConfig

internal sealed class ConfirmButtonUiState

internal open class UIProgress : ConfirmButtonUiState() {
    data class FingerprintRequired(val validationData: SecurityValidationData) : UIProgress()
    data class ButtonLoadingStarted(val timeOut: Int, val buttonConfig: ButtonConfig) : UIProgress()
    data class ButtonLoadingFinished(val explodeDecorator: ExplodeDecorator? = null) : UIProgress()
    object ButtonLoadingCanceled : UIProgress()
}

internal open class UIResult : ConfirmButtonUiState() {
    object VisualProcessorResult : UIResult()
}

internal open class UIError : ConfirmButtonUiState() {
    class ConnectionError(retriesCount: Int) : UIError() {
        private val maxRetries = 3
        val message = if (retriesCount <= maxRetries) R.string.px_connectivity_neutral_error else R.string.px_connectivity_error
        val actionMessage = if (retriesCount > maxRetries) R.string.px_snackbar_error_action else null
    }
    object BusinessError : UIError()
    class NotRecoverableError(val error: Throwable) : UIError()
}