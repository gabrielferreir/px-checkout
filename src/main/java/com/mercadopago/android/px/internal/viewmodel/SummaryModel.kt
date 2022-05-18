package com.mercadopago.android.px.internal.viewmodel

import com.mercadopago.android.px.internal.view.SummaryView
import com.mercadopago.android.px.model.internal.Application

internal data class SummaryModel(
    private var defaultKey: String,
    val summaryViewModelMap: Map<String, SummaryView.Model>
) {

    fun update(application: Application) {
        defaultKey = application.paymentMethod.type
    }

    fun getCurrent() = summaryViewModelMap[defaultKey]
}