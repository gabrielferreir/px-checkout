package com.mercadopago.android.px.internal.features.generic_modal

interface ViewModel {
    fun onButtonClicked(actionable: Actionable, isMain: Boolean)
    fun onDialogDismissed()
}