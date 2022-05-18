package com.mercadopago.android.px.core.v2

import android.os.Parcelable
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.preferences.CheckoutPreference
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethodsData(
    val paymentDataList: List<PaymentData>,
    val checkoutPreference: CheckoutPreference,
    val securityType: String,
    val validationProgramId: String? = null
): Parcelable
