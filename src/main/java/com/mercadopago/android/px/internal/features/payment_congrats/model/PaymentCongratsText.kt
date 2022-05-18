package com.mercadopago.android.px.internal.features.payment_congrats.model

import android.os.Parcelable
import com.mercadopago.android.px.model.internal.Text
import com.mercadopago.android.px.model.internal.TextAlignment
import kotlinx.android.parcel.Parcelize

private const val REGULAR = "regular"

@Parcelize
data class PaymentCongratsText @JvmOverloads constructor(
    val message: String = "",
    val backgroundColor: String? = null,
    val textColor: String? = null,
    val weight: String? = REGULAR,
    val alignment: TextAlignment = TextAlignment.LEFT,
    val fontSize: Float? = null
) : Parcelable {
    companion object {
        val EMPTY = PaymentCongratsText()

        fun from(text: Text) = text.run {
            PaymentCongratsText(
                message,
                backgroundColor,
                textColor,
                weight ?: REGULAR,
                alignment ?: TextAlignment.LEFT
            )
        }
    }
}
