package com.mercadopago.android.px.tracking.internal.views

import com.mercadopago.android.px.addons.model.internal.Experiment
import com.mercadopago.android.px.model.DiscountConfigurationModel
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.preferences.CheckoutPreference
import com.mercadopago.android.px.tracking.internal.BankInfoHelper
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.mapper.FromApplicationToApplicationInfo
import com.mercadopago.android.px.tracking.internal.model.OneTapData

internal class OneTapViewTracker(
    fromApplicationToApplicationInfo: FromApplicationToApplicationInfo,
    oneTapItem: Iterable<OneTapItem?>?,
    checkoutPreference: CheckoutPreference,
    discountModel: DiscountConfigurationModel,
    cardsWithEsc: Set<String?>,
    cardsWithSplit: Set<String?>,
    disabledMethodsQuantity: Int,
    private val experiments: List<Experiment>,
    bankInfoHelper: BankInfoHelper
) : TrackWrapper() {

    private val data = OneTapData.createFrom(fromApplicationToApplicationInfo, oneTapItem, checkoutPreference,
        discountModel, cardsWithEsc, cardsWithSplit, disabledMethodsQuantity, bankInfoHelper)

    override fun getTrack() = TrackFactory.withView(PATH_REVIEW_ONE_TAP_VIEW)
        .addData(data.toMap())
        .addExperiments(experiments)
        .build()

    companion object {
        const val PATH_REVIEW_ONE_TAP_VIEW = "$BASE_PATH/review/one_tap"
    }
}