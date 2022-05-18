package com.mercadopago.android.px.internal.viewmodel

import android.view.View

internal data class SummaryRowIconDescriptor(
    val drawable: IDetailDrawable,
    val drawableColor: IDetailColor,
    val listener: View.OnClickListener
)
