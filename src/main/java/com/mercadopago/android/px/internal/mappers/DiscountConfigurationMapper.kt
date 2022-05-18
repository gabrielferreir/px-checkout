package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.viewmodel.DiscountBody
import com.mercadopago.android.px.internal.viewmodel.DiscountDetailModel
import com.mercadopago.android.px.internal.viewmodel.DiscountHeader
import com.mercadopago.android.px.model.DiscountDescription

internal object DiscountConfigurationMapper: Mapper<DiscountDescription, DiscountDetailModel>() {

    override fun map(model: DiscountDescription): DiscountDetailModel {
        return DiscountDetailModel(
                DiscountHeader(model.title, model.subtitle, model.badge),
                DiscountBody(model.summary, model.description, model.legalTerms)
        )
    }
}
