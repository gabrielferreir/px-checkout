package com.mercadopago.android.px.internal.features.payment_result.presentation

import android.os.Parcelable
import com.mercadopago.android.px.internal.viewmodel.LazyString
import com.mercadopago.android.px.model.ExitAction
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class PaymentResultButton(
    val type: Type,
    val label: LazyString,
    val action: Action?,
    val target: String?,
    val exitAction: ExitAction? = null
) : Parcelable {

    constructor(type: Type, label: LazyString, action: Action): this(type, label, action, null)
    constructor(type: Type, label: LazyString, target: String): this(type, label, null, target)
    constructor(type: Type, exitAction: ExitAction): this(type, LazyString(exitAction.name), null, null, exitAction)

    enum class Action {
        CONTINUE,
        KYC,
        CHANGE_PM,
        PAY,
        MODAL_PAY,
        MODAL_CHANGE_PM
    }

    enum class Type {
        LOUD,
        QUIET,
        TRANSPARENT
    }
}
