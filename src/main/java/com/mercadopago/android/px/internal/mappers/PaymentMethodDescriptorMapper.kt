package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.datasource.CustomOptionIdSolver
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.repository.*
import com.mercadopago.android.px.internal.view.BankTransferDescriptorModel
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorView
import com.mercadopago.android.px.internal.viewmodel.*
import com.mercadopago.android.px.model.AmountConfiguration
import com.mercadopago.android.px.model.PayerCost
import com.mercadopago.android.px.model.PaymentTypes.*
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.model.one_tap.CheckoutBehaviour
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorModelByApplication as Model

internal class PaymentMethodDescriptorMapper(
    private val paymentSettings: PaymentSettingRepository,
    private val amountConfigurationRepository: AmountConfigurationRepository,
    private val disabledPaymentMethodRepository: DisabledPaymentMethodRepository,
    private val applicationSelectionRepository: ApplicationSelectionRepository,
    private val amountRepository: AmountRepository
) : Mapper<OneTapItem, Model>() {

    override fun map(value: OneTapItem): Model {
        val currency = paymentSettings.currency

        return Model(applicationSelectionRepository[value].paymentMethod.type).also { model ->
            value.getApplications().forEach { application ->
                val customOptionIdByApplication = CustomOptionIdSolver.getByApplication(value, application)
                val paymentTypeId = application.paymentMethod.type
                val payerPaymentMethodKey = PayerPaymentMethodKey(customOptionIdByApplication, paymentTypeId)

                val defaultBehaviour = value.getBehaviour(CheckoutBehaviour.Type.TAP_CARD)
                val hasBehaviour = (application.behaviours[CheckoutBehaviour.Type.TAP_CARD] ?: defaultBehaviour) != null

                val descriptorModel = when {
                    disabledPaymentMethodRepository.hasKey(payerPaymentMethodKey) ->
                        DisabledPaymentMethodDescriptorModel.createFrom(application.status.mainMessage)
                    isCreditCardPaymentType(paymentTypeId) || value.isConsumerCredits ->
                        getAmountConfiguration(payerPaymentMethodKey)?.let {
                            mapCredit(value, it)
                        }
                    isCardPaymentType(paymentTypeId) ->
                        getAmountConfiguration(payerPaymentMethodKey)?.let {
                            DebitCardDescriptorModel.createFrom(currency, it)
                        }
                    isAccountMoney(value.paymentMethodId) ->
                        AccountMoneyDescriptorModel.createFrom(value.accountMoney, currency,
                            amountRepository.getAmountToPay(value.paymentTypeId, null as PayerCost?))
                    isBankTransfer(paymentTypeId) -> {
                        val bankTransferSliderTitle = value.bankTransfer?.displayInfo?.sliderTitle
                        if(bankTransferSliderTitle.isNotNullNorEmpty()) {
                            BankTransferDescriptorModel.createFrom(bankTransferSliderTitle)
                        }
                        else {
                            EmptyInstallmentsDescriptorModel.create()
                        }
                    }
                    else -> EmptyInstallmentsDescriptorModel.create()
                } ?: EmptyInstallmentsDescriptorModel.create()

                if (getAmountConfiguration(payerPaymentMethodKey)?.payerCosts.isNullOrEmpty()) {
                    descriptorModel.setHasBehaviour(hasBehaviour)
                }

                model[application] = descriptorModel
            }
        }
    }

    private fun getAmountConfiguration(payerPaymentMethodKey: PayerPaymentMethodKey): AmountConfiguration? {
        return amountConfigurationRepository.getConfigurationFor(payerPaymentMethodKey)
    }

    private fun mapCredit(oneTapItem: OneTapItem, amountConfiguration: AmountConfiguration)
        : PaymentMethodDescriptorView.Model {
        //This model is useful for Credit Card and Consumer Credits
        // FIXME change model to represent more than just credit cards.
        val installmentsRightHeader = if (oneTapItem.hasBenefits()) oneTapItem.benefits.installmentsHeader else null
        val interestFree = if (oneTapItem.hasBenefits()) oneTapItem.benefits.interestFree else null
        return CreditCardDescriptorModel
            .createFrom(paymentSettings.currency, installmentsRightHeader, interestFree, amountConfiguration)
    }

}