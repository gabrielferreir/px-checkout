package com.mercadopago.android.px.internal.di

import com.mercadopago.android.px.internal.features.AmountDescriptorViewModelFactory
import com.mercadopago.android.px.internal.features.SummaryRowTextDescriptorFactory

internal object FactoryProvider {
    val amountDescriptorViewModelFactory: AmountDescriptorViewModelFactory
        get() = AmountDescriptorViewModelFactory(
            SummaryRowTextDescriptorFactory(Session.getInstance().configurationModule.paymentSettings.currency),
            Session.getInstance().experimentsRepository
        )
}