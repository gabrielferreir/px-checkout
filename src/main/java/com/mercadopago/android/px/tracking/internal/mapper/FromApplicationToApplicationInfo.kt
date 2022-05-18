package com.mercadopago.android.px.tracking.internal.mapper

import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.tracking.internal.model.ApplicationInfo

internal class FromApplicationToApplicationInfo : Mapper<Application, ApplicationInfo>() {

    override fun map(value: Application): ApplicationInfo {
        return ApplicationInfo(
            value.paymentMethod.id,
            value.paymentMethod.type,
            value.status.isEnabled,
            value.status.detail,
            value.validationPrograms?.map {
                ApplicationInfo.ValidationProgramInfo(it.id, it.mandatory)
            } ?: listOf())
    }
}