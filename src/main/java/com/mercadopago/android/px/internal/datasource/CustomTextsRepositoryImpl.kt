package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.repository.CustomTextsRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.model.internal.AdditionalInfo
import com.mercadopago.android.px.model.internal.CustomTexts

internal class CustomTextsRepositoryImpl(paymentSettings: PaymentSettingRepository) : CustomTextsRepository {

    override val customTexts: CustomTexts =
        AdditionalInfo.newInstance(paymentSettings.checkoutPreference?.additionalInfo)?.customTexts ?:
            with(paymentSettings.advancedConfiguration.customStringConfiguration) {
                CustomTexts(customPayButtonText, customPayButtonProgressText, totalDescriptionText)
            }
}