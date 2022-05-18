package com.mercadopago.android.px.internal.features.business_result

import com.mercadolibre.android.mlbusinesscomponents.components.actioncard.MLBusinessActionCardViewData
import com.mercadopago.android.px.internal.extensions.orIfEmpty
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsResponse

internal object MLBusinessMapper {

    @JvmStatic
    fun map(model: PaymentCongratsResponse.ExpenseSplit?): MLBusinessActionCardViewData? = model?.takeIf { it.title != null }
            ?.run {
                object : MLBusinessActionCardViewData {
                    override fun getAffordanceText() = action.label.orEmpty()

                    override fun getImageUrl() = imageUrl.orEmpty()

                    override fun getTitle() = title.message.orEmpty()

                    override fun getTitleBackgroundColor() = title.backgroundColor.orIfEmpty("#ffffff").toString()

                    override fun getTitleColor() = title.textColor.orIfEmpty("#ffffff").toString()

                    override fun getTitleWeight() = title.weight.orEmpty()
                }
            }
}