package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.OneTapItem

typealias PayerPaymentTypeId = String

internal interface ApplicationSelectionRepository : LocalRepository<MutableMap<PayerPaymentTypeId, Application>> {
    operator fun get(payerPaymentMethodId: String): Application
    operator fun get(oneTapItem: OneTapItem): Application
    operator fun set(oneTapItem: OneTapItem, application: Application)
}