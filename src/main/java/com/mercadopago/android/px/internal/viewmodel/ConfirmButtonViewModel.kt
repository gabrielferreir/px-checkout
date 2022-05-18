package com.mercadopago.android.px.internal.viewmodel

import com.mercadopago.android.px.model.internal.Application

internal data class ConfirmButtonViewModel(val isDisabled: Boolean) {

    class ByApplication {
        val values = mutableMapOf<String, ConfirmButtonViewModel>()

        operator fun set(application: Application?, confirmButtonViewModel: ConfirmButtonViewModel) {
            values[application?.paymentMethod?.type.orEmpty()] = confirmButtonViewModel
        }

        operator fun get(application: Application?): ConfirmButtonViewModel {
            return values[application?.paymentMethod?.type.orEmpty()] ?: ConfirmButtonViewModel(true)
        }
    }
}