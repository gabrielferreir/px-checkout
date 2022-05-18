package com.mercadopago.android.px.internal.features

import com.mercadopago.android.px.model.internal.CheckoutFeatures

internal interface FeatureProvider {
    val availableFeatures: CheckoutFeatures
}
