package com.mercadopago.android.px.internal.features.payment_congrats.model

import android.os.Parcel
import android.os.Parcelable
import com.mercadopago.android.px.internal.extensions.readBigDecimal
import com.mercadopago.android.px.internal.extensions.writeBigDecimal
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.internal.util.TextUtil
import java.math.BigDecimal

data class PXPaymentCongratsTracking(
    val campaignId: String?,
    val currencyId: String?,
    val paymentStatus: String?,
    val paymentStatusDetail: String?,
    val paymentId: Long?,
    val totalAmount: BigDecimal?,
    val flowExtraInfo: Map<String, Any>? = HashMap(),
    val flow: String?,
    val sessionId: String?,
    val paymentMethodId: String?,
    val paymentMethodType: String?
) : Parcelable {

    constructor(
        campaignId: String?,
        currencyId: String?,
        paymentStatusDetail: String?,
        paymentId: Long?,
        totalAmount: BigDecimal?,
        flowExtraInfo: Map<String, Any>? = HashMap(),
        flow: String?,
        sessionId: String?,
        paymentMethodId: String?,
        paymentMethodType: String?
    ) : this(campaignId, currencyId, TextUtil.EMPTY, paymentStatusDetail, paymentId, totalAmount, flowExtraInfo, flow, sessionId,
        paymentMethodId, paymentMethodType)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readBigDecimal(),
        JsonUtil.getMapFromJson(parcel.readString()),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(campaignId)
        parcel.writeString(currencyId)
        parcel.writeString(paymentStatus)
        parcel.writeString(paymentStatusDetail)
        parcel.writeValue(paymentId)
        parcel.writeBigDecimal(totalAmount)
        parcel.writeString(JsonUtil.toJson(flowExtraInfo))
        parcel.writeString(flow)
        parcel.writeString(sessionId)
        parcel.writeString(paymentMethodId)
        parcel.writeString(paymentMethodType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PXPaymentCongratsTracking> {
        override fun createFromParcel(parcel: Parcel): PXPaymentCongratsTracking {
            return PXPaymentCongratsTracking(parcel)
        }

        override fun newArray(size: Int): Array<PXPaymentCongratsTracking?> {
            return arrayOfNulls(size)
        }
    }
}
