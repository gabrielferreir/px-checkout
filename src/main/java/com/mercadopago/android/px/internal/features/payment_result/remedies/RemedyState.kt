package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.internal.features.payment_result.remedies.view.HighRiskRemedy
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.RetryPaymentFragment
import com.mercadopago.android.px.model.internal.Modal
import com.mercadopago.android.px.model.internal.OneTapItem

internal sealed class RemedyState {
    internal data class ShowRetryPaymentRemedy(val data: Pair<RetryPaymentFragment.Model, OneTapItem?>) : RemedyState()
    internal data class ShowKyCRemedy(val model: HighRiskRemedy.Model) : RemedyState()
    internal data class GoToKyc(val deepLink: String) : RemedyState()
    internal object ChangePaymentMethod : RemedyState()
    internal data class ShowModal(val modal: Modal) : RemedyState()
    internal object Pay : RemedyState()
}