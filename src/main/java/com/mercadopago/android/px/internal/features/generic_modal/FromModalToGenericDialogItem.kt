package com.mercadopago.android.px.internal.features.generic_modal

import com.mercadopago.android.px.internal.viewmodel.TextLocalized
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.internal.Modal

internal class FromModalToGenericDialogItem : Mapper<FromModalToGenericDialogItem.Params, GenericDialogItem>() {
    override fun map(value: Params): GenericDialogItem {
        val modal = value.modal
        return GenericDialogItem(
            value.dialogDescription,
            modal.imageUrl,
            TextLocalized(modal.title, 0),
            TextLocalized(modal.description, 0),
            modal.mainButton.let { Actionable(it.label, it.target, value.action) },
            modal.secondaryButton?.let { Actionable(it.label, it.target, value.action) })
    }

    data class Params(
        val action: ActionType,
        val dialogDescription: String,
        val modal: Modal
    )
}
