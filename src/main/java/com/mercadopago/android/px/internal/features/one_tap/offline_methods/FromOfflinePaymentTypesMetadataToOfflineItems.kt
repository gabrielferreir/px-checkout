package com.mercadopago.android.px.internal.features.one_tap.offline_methods

import com.mercadopago.android.px.model.OfflinePaymentType
import java.util.*

internal object FromOfflinePaymentTypesMetadataToOfflineItems {
    @JvmStatic
    fun map(offlinePaymentTypes: List<OfflinePaymentType>): List<OfflineMethodItem> {
        val offlineMethodItems: MutableList<OfflineMethodItem> = ArrayList()
        for (offlinePaymentType in offlinePaymentTypes) {
            offlineMethodItems.add(OfflineMethodItem(offlinePaymentType.name))
            for (offlinePaymentMethod in offlinePaymentType.paymentMethods) {
                if (offlinePaymentMethod.status.isEnabled) {
                    offlineMethodItems.add(
                        OfflineMethodItem(offlinePaymentMethod.name, offlinePaymentMethod.id,
                            offlinePaymentMethod.instructionId, offlinePaymentMethod.description,
                            offlinePaymentMethod.imageUrl, offlinePaymentMethod.isAdditionalInfoNeeded))
                }
            }
        }
        return offlineMethodItems
    }
}