package com.mercadopago.android.px.internal.base.use_case

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.datasource.mapper.FromPayerPaymentMethodToCardMapper
import com.mercadopago.android.px.internal.mappers.PaymentMethodMapper
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey
import com.mercadopago.android.px.internal.repository.PaymentMethodRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.tracking.internal.MPTracker

internal class UserSelectionUseCase(
    private val userSelectionRepository: UserSelectionRepository,
    private val amountConfigurationRepository: AmountConfigurationRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val fromPayerPaymentMethodToCardMapper: FromPayerPaymentMethodToCardMapper,
    private val paymentMethodMapper: PaymentMethodMapper,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<PaymentConfiguration, Unit>(tracker) {

    override suspend fun doExecute(param: PaymentConfiguration): Response<Unit, MercadoPagoError> {
        val paymentMethod = paymentMethodMapper.map(Pair(param.paymentMethodId, param.paymentTypeId))

        userSelectionRepository.select(paymentMethod, null)
        userSelectionRepository.select(param.customOptionId)

        if (PaymentTypes.isCardPaymentType(paymentMethod.paymentTypeId)) {

            val card = fromPayerPaymentMethodToCardMapper.map(
                PayerPaymentMethodKey(param.customOptionId, paymentMethod.paymentTypeId)
            )

            checkNotNull(card) { "Cannot find selected card" }

            if (param.splitPayment) {
                val amountConfiguration = amountConfigurationRepository.getConfigurationSelectedFor(card.id!!)

                checkNotNull(amountConfiguration) { "Cannot find amount configuration for selected card" }

                val splitConfiguration = amountConfiguration.splitConfiguration

                checkNotNull(splitConfiguration) { "Cannot find split configuration for selected card" }

                val secondaryPaymentMethodId = splitConfiguration.secondaryPaymentMethod.paymentMethodId
                val secondaryPaymentMethod = paymentMethodRepository.getPaymentMethodById(secondaryPaymentMethodId)
                userSelectionRepository.select(card, secondaryPaymentMethod)
            } else {
                userSelectionRepository.select(card, null)
            }
        }

        param.payerCost?.apply(userSelectionRepository::select)

        return Response.Success(Unit)
    }
}