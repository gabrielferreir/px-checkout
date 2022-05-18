package com.mercadopago.android.px.internal.features.payment_result.remedies

import android.os.Parcelable
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.HighRiskRemedy
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.RetryPaymentFragment
import com.mercadopago.android.px.internal.viewmodel.PaymentResultType
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class RemediesModel(
    val header: HeaderModel?,
    val retryPayment: RetryPaymentFragment.Model?,
    val highRisk: HighRiskRemedy.Model?,
    val trackingData: Map<String, String>?
) : Parcelable {

    @Parcelize
    data class HeaderModel(
        val title: String,
        val iconUrl: String,
        val badgeUrl: String
    ) : Parcelable

    fun hasRemedies() = retryPayment != null || highRisk != null

    companion object {
        @JvmField
        val DECORATOR = PaymentResultType.PENDING
    }
}
