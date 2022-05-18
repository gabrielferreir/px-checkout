package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.helper.SecurityCodeHelper
import com.mercadopago.android.px.internal.mappers.NonNullMapper
import com.mercadopago.android.px.internal.model.EscStatus
import com.mercadopago.android.px.internal.repository.OneTapItemRepository
import com.mercadopago.android.px.model.CustomSearchItem
import com.mercadopago.android.px.model.internal.remedies.Installment
import com.mercadopago.android.px.model.internal.remedies.RemedyPaymentMethod

internal class AlternativePayerPaymentMethodsMapper(
    private val oneTapItemRepository: OneTapItemRepository,
    private val escManagerBehaviour: ESCManagerBehaviour) :
    NonNullMapper<CustomSearchItem, RemedyPaymentMethod>() {

    override fun map(value: CustomSearchItem): RemedyPaymentMethod {
        val securityCode = oneTapItemRepository[value.id].card?.displayInfo?.securityCode
        val cvvIsOptional = !SecurityCodeHelper.isRequired(securityCode)
        return RemedyPaymentMethod(
            value.id,
            null,
            value.issuer?.name,
            value.firstSixDigits,
            value.lastFourDigits,
            value.paymentMethodId,
            value.type,
            securityCode?.length,
            securityCode?.cardLocation,
            null,
            value.getAmountConfiguration(value.defaultAmountConfiguration!!)
                .payerCosts
                .map { payerCost -> Installment(payerCost.installments, payerCost.totalAmount) },
            //Hacks to avoid asking for cvv in remedy when it's optional
            value.escStatus ?: if (cvvIsOptional) EscStatus.APPROVED else EscStatus.NOT_AVAILABLE,
            escManagerBehaviour.escCardIds.contains(value.id) || cvvIsOptional, null, value.paymentMethodName, value.bankInfo)
    }
}
