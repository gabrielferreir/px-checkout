package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.extensions.orIfEmpty
import com.mercadopago.android.px.model.internal.AdditionalInfo
import com.mercadopago.android.px.model.internal.SummaryInfo
import com.mercadopago.android.px.preferences.CheckoutPreference

internal class SummaryInfoMapper : Mapper<CheckoutPreference, SummaryInfo>() {

    override fun map(value: CheckoutPreference): SummaryInfo {
        return AdditionalInfo.newInstance(value.additionalInfo)?.summaryInfo ?:
        value.items[0].run { SummaryInfo(description.orIfEmpty(title), pictureUrl) }
    }
}
