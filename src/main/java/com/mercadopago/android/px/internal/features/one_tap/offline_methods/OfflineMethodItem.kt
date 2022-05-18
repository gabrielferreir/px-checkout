package com.mercadopago.android.px.internal.features.one_tap.offline_methods

import com.mercadopago.android.px.model.internal.Text

internal class OfflineMethodItem {
    val name: Text
    val paymentMethodId: String?
    val paymentTypeId: String?
    val description: Text?
    val isAdditionalInfoNeeded: Boolean
    val isOfflinePaymentTypeItem: Boolean
        get() = description == null && paymentMethodId == null
    var imageUrl: String?

    constructor(name: Text) {
        this.name = name
        this.paymentMethodId = null
        this.paymentTypeId = null
        this.description = null
        this.imageUrl = null
        isAdditionalInfoNeeded = false
    }

    constructor(name: Text, paymentMethodId: String,
        paymentTypeId: String, description: Text, imageUrl: String,
        additionalInfoNeeded: Boolean) {
        this.name = name
        this.paymentMethodId = paymentMethodId
        this.paymentTypeId = paymentTypeId
        this.description = description
        this.imageUrl = imageUrl
        isAdditionalInfoNeeded = additionalInfoNeeded
    }
}