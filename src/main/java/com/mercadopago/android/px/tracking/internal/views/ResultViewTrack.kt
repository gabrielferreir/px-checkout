package com.mercadopago.android.px.tracking.internal.views

import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemedyType
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.PaymentResult
import com.mercadopago.android.px.model.internal.remedies.RemediesResponse
import com.mercadopago.android.px.tracking.internal.BankInfoHelper
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.RemedyTrackData
import com.mercadopago.android.px.tracking.internal.model.ResultViewTrackModel
import java.util.*

internal class ResultViewTrack : TrackWrapper {
    private val resultViewTrackModel: ResultViewTrackModel
    private val relativePath: String
    private val remediesResponse: RemediesResponse
    private var isStandaloneCongrats: Boolean = false

    constructor(paymentModel: PaymentModel, screenConfiguration: PaymentResultScreenConfiguration,
                paymentSetting: PaymentSettingRepository, isMP: Boolean, bankInfoHelper: BankInfoHelper) {
        resultViewTrackModel = ResultViewTrackModel(paymentModel, screenConfiguration, paymentSetting.checkoutPreference!!,
                paymentSetting.currency.id, isMP, bankInfoHelper)
        relativePath = getMappedResult(paymentModel.paymentResult)
        this.remediesResponse = paymentModel.remedies
    }

    constructor(paymentModel: PaymentCongratsModel, isMP: Boolean, bankInfoHelper: BankInfoHelper) {
        isStandaloneCongrats = paymentModel.isStandAloneCongrats
        resultViewTrackModel = ResultViewTrackModel(paymentModel, isMP, bankInfoHelper)
        relativePath = paymentModel.trackingRelativePath
        this.remediesResponse = RemediesResponse.EMPTY
    }

    override fun getTrack() = TrackFactory.withView(getViewPath()).addData(getData()).build()

    private fun getData(): Map<String, Any> {
        val map = resultViewTrackModel.toMap()
        if (relativePath == ERROR && !isStandaloneCongrats) {
            map["remedies"] = getRemedies()
        }
        return map
    }

    private fun getViewPath() = String.format(Locale.US,
        if (isStandaloneCongrats) STANDALONE_PATH else CHECKOUT_PATH, relativePath)

    private fun getMappedResult(payment: PaymentResult): String {
        return when {
            payment.isApproved || payment.isInstructions -> SUCCESS
            payment.isRejected -> ERROR
            payment.isPending -> PENDING
            else -> UNKNOWN
        }
    }

    private fun getRemedies(): List<RemedyTrackData>? {
        val remedies = mutableListOf<RemedyTrackData>()
        when {
            remediesResponse.suggestedPaymentMethod != null -> {
                remedies.add(getRemedyData(RemedyType.PAYMENT_METHOD_SUGGESTION))
            }
            remediesResponse.cvv != null -> {
                remedies.add(getRemedyData(RemedyType.CVV_REQUEST))
            }
            remediesResponse.highRisk != null -> {
                remedies.add(getRemedyData(RemedyType.KYC_REQUEST))
            }
        }
        return remedies
    }

    private fun getRemedyData(type: RemedyType) = RemedyTrackData(type.getType(), remediesResponse.trackingData)

    companion object {
        private const val PATH = "/result/%s"
        private const val CHECKOUT_PATH = "$BASE_PATH$PATH"
        private const val STANDALONE_PATH = "/payment_congrats$PATH"
        const val SUCCESS = "success"
        const val PENDING = "further_action_needed"
        const val ERROR = "error"
        const val UNKNOWN = "unknown"
    }
}