package com.mercadopago.android.px.internal.features.generic_modal

sealed class GenericDialogState {
    class LoadView(val item: GenericDialogItem): GenericDialogState()
    class ButtonClicked(val action: GenericDialogAction): GenericDialogState()
}