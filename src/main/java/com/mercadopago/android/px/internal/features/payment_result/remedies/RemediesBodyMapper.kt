package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.internal.repository.AmountRepository
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.internal.remedies.CustomStringConfiguration
import com.mercadopago.android.px.model.internal.remedies.RemediesBody
import com.mercadopago.android.px.model.internal.remedies.RemedyPaymentMethod

internal class RemediesBodyMapper(private val userSelectionRepository: UserSelectionRepository,
    private val amountRepository: AmountRepository, private val customOptionId: String,
    private val esc: Boolean, private val alternativePayerPaymentMethods: List<RemedyPaymentMethod>,
    private val paymentSettingRepository: PaymentSettingRepository,
    private val payerPaymentMethodRepository: PayerPaymentMethodRepository
) : Mapper<PaymentData, RemediesBody>() {

    override fun map(data: PaymentData): RemediesBody {
        var secCodeLocation: String? = null
        var secCodeLength: Int? = null
        var escStatus: String? = null
        var bin: String? = null
        var lastFourDigits: String? = null

        userSelectionRepository.card?.let {
            secCodeLocation = it.getSecurityCodeLocation()
            secCodeLength = it.getSecurityCodeLength()
            escStatus = it.escStatus
            bin = it.firstSixDigits
            lastFourDigits = it.lastFourDigits
        } ?: data.token?.let {
            secCodeLocation = DEFAULT_CVV_LOCATION
            secCodeLength = it.getSecurityCodeLength()
            escStatus = null
            bin = it.firstSixDigits
            lastFourDigits = it.lastFourDigits
        }

        val payerPaymentMethod = userSelectionRepository.customOptionId?.let { payerPaymentMethodRepository[it] }

        with(data) {
            val payerPaymentMethodRejected = RemedyPaymentMethod(customOptionId, payerCost?.installments,
                issuer?.name, bin, lastFourDigits, paymentMethod.id, paymentMethod.paymentTypeId,
                secCodeLength, secCodeLocation, amountRepository.getAmountToPay(paymentMethod.paymentTypeId, payerCost),
                null, escStatus, esc, null, payerPaymentMethod?.paymentMethodName, payerPaymentMethod?.bankInfo)
            val customStringConfiguration = paymentSettingRepository.advancedConfiguration.customStringConfiguration
            return RemediesBody(payerPaymentMethodRejected, alternativePayerPaymentMethods,
                with(customStringConfiguration) {
                    CustomStringConfiguration(customPayButtonText, customPayButtonProgressText, totalDescriptionText)
                })
        }
    }

    companion object {
        private const val DEFAULT_CVV_LOCATION = "back"
    }
}