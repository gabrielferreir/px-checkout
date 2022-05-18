package com.mercadopago.android.px.internal.features.one_tap.add_new_card.sheet_options

import android.os.Parcelable
import com.mercadopago.android.px.model.internal.CardFormOption
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardFormBottomSheetModel(
    val titleHeader: String,
    val cardFormOptions: List<CardFormOption>): Parcelable