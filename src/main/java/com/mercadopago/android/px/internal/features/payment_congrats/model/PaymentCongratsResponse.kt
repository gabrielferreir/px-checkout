package com.mercadopago.android.px.internal.features.payment_congrats.model

import android.os.Parcelable
import com.mercadolibre.android.andesui.message.AndesMessage
import com.mercadolibre.android.andesui.message.hierarchy.AndesMessageHierarchy
import com.mercadolibre.android.andesui.message.type.AndesMessageType
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PaymentCongratsResponse(
    val loyalty: Loyalty? = null,
    val discount: Discount? = null,
    val expenseSplit: ExpenseSplit? = null,
    private val crossSellings: List<CrossSelling>? = emptyList(),
    val viewReceipt: Action? = null,
    private val customOrder: Boolean = false,
    val backUrl: String? = null,
    val redirectUrl: String? = null,
    val autoReturn: AutoReturn? = null,
    val operationInfo: OperationInfo? = null): Parcelable {

    fun getCrossSellings() = crossSellings ?: emptyList()
    fun hasCustomOrder() = customOrder

    @Parcelize
    data class Loyalty(
        val progress: Progress,
        val title: String,
        val action: Action): Parcelable {

        @Parcelize
        data class Progress(
            val percentage: Float,
            val color: String?,
            val level: Int
        ): Parcelable
    }

    @Parcelize
    data class Discount(
        val title: String,
        val subtitle: String?,
        val action: Action,
        val actionDownload: DownloadApp?,
        val touchpoint: PXBusinessTouchpoint?,
        private val items: List<Item>?): Parcelable {

        fun getItems() = items ?: emptyList()

        @Parcelize
        data class DownloadApp(
            val title: String,
            val action: Action
        ): Parcelable

        @Parcelize
        data class Item(
            val title: String,
            val subtitle: String?,
            val icon: String,
            val target: String,
            val campaignId: String?
        ): Parcelable
    }

    @Parcelize
    data class CrossSelling(
        val title: String,
        val icon: String,
        val action: Action,
        val contentId: String
    ): Parcelable

    @Parcelize
    data class PXBusinessTouchpoint(
        val id: String,
        val type: String,
        val content: @RawValue Map<*, *>?,
        val tracking: @RawValue Map<String, Any>?,
        val additionalEdgeInsets: AdditionalEdgeInsets?
    ): Parcelable

    @Parcelize
    data class AdditionalEdgeInsets(
        val top: Int,
        val left: Int,
        val bottom: Int,
        val right: Int
    ): Parcelable

    @Parcelize
    data class ExpenseSplit(
        val title: PaymentCongratsText,
        val action: Action,
        val imageUrl: String
    ): Parcelable

    @Parcelize
    data class Action(
        val label: String,
        val target: String
    ): Parcelable

    @Parcelize
    data class AutoReturn(
        val label: String? = null,
        val seconds: Int? = null
    ) : Parcelable

    @Parcelize
    data class OperationInfo(
        val hierarchy: AndesMessageHierarchy,
        val type: AndesMessageType,
        val body: String
    ) : Parcelable

    companion object {
        @JvmField val EMPTY = PaymentCongratsResponse()
    }
}
