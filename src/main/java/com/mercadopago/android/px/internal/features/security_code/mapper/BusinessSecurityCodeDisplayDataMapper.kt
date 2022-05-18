package com.mercadopago.android.px.internal.features.security_code.mapper

import com.mercadopago.android.px.internal.features.security_code.data.SecurityCodeDisplayData
import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessCardDisplayInfo
import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessSecurityCodeDisplayData
import com.mercadopago.android.px.internal.mappers.Mapper

internal class BusinessSecurityCodeDisplayDataMapper: Mapper<SecurityCodeDisplayData, BusinessSecurityCodeDisplayData>()
{
    override fun map(value: SecurityCodeDisplayData) = value.run {

        val businessCardDisplayInfo = cardDisplayInfo?.let { displayInfo ->
            BusinessCardDisplayInfo(
                displayInfo.cardholderName,
                displayInfo.expiration,
                displayInfo.color,
                displayInfo.fontColor,
                displayInfo.issuerId,
                displayInfo.cardPattern,
                displayInfo.getCardPattern(),
                displayInfo.securityCode.cardLocation,
                displayInfo.securityCode.length,
                displayInfo.lastFourDigits,
                displayInfo.paymentMethodImage,
                displayInfo.issuerImage,
                displayInfo.fontType,
                displayInfo.paymentMethodImageUrl,
                displayInfo.issuerImageUrl,
                displayInfo.type
            )
        }

        BusinessSecurityCodeDisplayData(
            title,
            message,
            value.securityCodeLength,
            businessCardDisplayInfo
        )
    }
}