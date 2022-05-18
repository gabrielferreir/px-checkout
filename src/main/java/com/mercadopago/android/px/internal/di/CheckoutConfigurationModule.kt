package com.mercadopago.android.px.internal.di

import android.content.Context
import com.mercadopago.android.px.core.internal.FlowConfigurationProvider
import com.mercadopago.android.px.internal.datasource.*
import com.mercadopago.android.px.internal.repository.*

internal class CheckoutConfigurationModule(context: Context) : ConfigurationModule(context) {

    val userSelectionRepository: UserSelectionRepository by lazy {
        UserSelectionService(sharedPreferences,
            fileManager)
    }
    val paymentSettings: PaymentSettingRepository by lazy { PaymentSettingService(sharedPreferences, fileManager) }
    val disabledPaymentMethodRepository: DisabledPaymentMethodRepository by lazy {
        DisabledPaymentMethodRepositoryImpl(fileManager)
    }
    val payerComplianceRepository: PayerComplianceRepository by lazy { PayerComplianceRepositoryImpl(fileManager) }
    private var internalChargeRepository: ChargeRepository? = null
    val chargeRepository: ChargeRepository
        get() {
            if (internalChargeRepository == null) {
                internalChargeRepository = ChargeService(sharedPreferences)
            }
            return internalChargeRepository!!
        }

    private var internalCustomTextsRepository: CustomTextsRepository? = null
    val customTextsRepository: CustomTextsRepository
        get() {
            if (internalCustomTextsRepository == null) {
                internalCustomTextsRepository = CustomTextsRepositoryImpl(paymentSettings)
            }
            return internalCustomTextsRepository!!
        }

    private var internalApplicationSelectionRepository: ApplicationSelectionRepository? = null
    val applicationSelectionRepository: ApplicationSelectionRepository
        get() {
            return internalApplicationSelectionRepository ?: ApplicationSelectionRepositoryImpl(
                fileManager, Session.getInstance().oneTapItemRepository).also {
                internalApplicationSelectionRepository = it
            }
        }

    private var internalPayerCostSelectionRepository: PayerCostSelectionRepository? = null
    val payerCostSelectionRepository: PayerCostSelectionRepository
        get() {
            return internalPayerCostSelectionRepository
                ?: PayerCostSelectionRepositoryImpl(sharedPreferences, applicationSelectionRepository).also {
                    internalPayerCostSelectionRepository = it
                }
        }
    private var internalFlowConfigurationProvider: FlowConfigurationProvider? = null
    val flowConfigurationProvider: FlowConfigurationProvider
        get() {
            if (internalFlowConfigurationProvider == null) {
                internalFlowConfigurationProvider = FlowConfigurationProvider(paymentSettings.paymentConfiguration)
            }

            return internalFlowConfigurationProvider!!
        }

    override fun reset() {
        super.reset()
        userSelectionRepository.reset()
        paymentSettings.reset()
        disabledPaymentMethodRepository.reset()
        payerComplianceRepository.reset()
        applicationSelectionRepository.reset()
        payerCostSelectionRepository.reset()
        chargeRepository.reset()
        internalChargeRepository = null
        internalCustomTextsRepository = null
        internalApplicationSelectionRepository = null
        internalPayerCostSelectionRepository = null
        internalFlowConfigurationProvider = null
    }
}