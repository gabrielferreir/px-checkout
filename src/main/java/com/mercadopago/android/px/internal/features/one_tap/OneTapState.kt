package com.mercadopago.android.px.internal.features.one_tap

import com.mercadopago.android.px.internal.base.BaseState
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class OneTapState @JvmOverloads constructor(
    var paymentMethodIndex: Int = 0,
    var splitSelectionState: SplitSelectionState = SplitSelectionState()
) : BaseState
