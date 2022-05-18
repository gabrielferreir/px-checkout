package com.mercadopago.android.px.internal.view

import com.mercadopago.android.px.model.internal.Application

internal class PaymentMethodDescriptorModelByApplication(private var defaultKey: String) {
    private val values = mutableMapOf<String, PaymentMethodDescriptorView.Model>()

    fun update(application: Application?) {
        defaultKey = application?.paymentMethod?.type.orEmpty()
    }

    operator fun set(application: Application?, model: PaymentMethodDescriptorView.Model) {
        values[application?.paymentMethod?.type.orEmpty()] = model
    }

    fun getCurrent(): PaymentMethodDescriptorView.Model {
        return values[defaultKey] ?: throw IllegalStateException("There is no model for current application")
    }
}