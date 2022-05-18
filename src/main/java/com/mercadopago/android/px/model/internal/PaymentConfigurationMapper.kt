package com.mercadopago.android.px.model.internal

import com.mercadopago.android.px.internal.datasource.CustomOptionIdSolver
import com.mercadopago.android.px.internal.helper.SecurityCodeHelper
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.internal.repository.PayerCostSelectionRepository
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState
import com.mercadopago.android.px.model.PayerCost
import com.mercadopago.android.px.model.PaymentMethods
import com.mercadopago.android.px.model.PaymentTypes

internal class PaymentConfigurationMapper(
    private val amountConfigurationRepository: AmountConfigurationRepository,
    private val payerCostSelectionRepository: PayerCostSelectionRepository,
    private val applicationSelectionRepository: ApplicationSelectionRepository,
    private val customOptionIdSolver: CustomOptionIdSolver
) : Mapper<PaymentConfigurationData, PaymentConfiguration>() {

    override fun map(value: PaymentConfigurationData): PaymentConfiguration {

        val customOptionId = customOptionIdSolver[value.oneTapItem]
        val (paymentMethodId, paymentTypeId) =
            with(applicationSelectionRepository[value.oneTapItem].paymentMethod) { id to type }

        var payerCost: PayerCost? = null
        val amountConfiguration = amountConfigurationRepository.getConfigurationSelectedFor(customOptionId)
        val splitPayment = value.splitSelectionState.userWantsToSplit() && amountConfiguration!!.allowSplit()

        if (PaymentTypes.isCardPaymentType(paymentTypeId) || PaymentMethods.CONSUMER_CREDITS == paymentMethodId) {
            payerCost = amountConfiguration!!.getCurrentPayerCost(value.splitSelectionState.userWantsToSplit(),
                payerCostSelectionRepository.get(customOptionId))
        }

        return PaymentConfiguration(paymentMethodId, paymentTypeId, customOptionId,
            SecurityCodeHelper.isRequired(value.oneTapItem.card?.displayInfo?.securityCode),
            splitPayment, payerCost)
    }
}

internal data class PaymentConfigurationData @JvmOverloads constructor(
    val oneTapItem: OneTapItem,
    val splitSelectionState: SplitSelectionState = SplitSelectionState()
)
