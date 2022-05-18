package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository
import com.mercadopago.android.px.internal.repository.DiscountRepository
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.DiscountConfigurationModel
import java.io.File

private const val DISCOUNT_CONFIGURATION = "discount_configuration_repository"

internal class DiscountServiceImpl(private val fileManager: FileManager,
    private val userSelectionRepository: UserSelectionRepository,
    private val amountConfigurationRepository: AmountConfigurationRepository,
    private val configurationSolver: ConfigurationSolver) :
    AbstractLocalRepository<Map<String, DiscountConfigurationModel>>(fileManager), DiscountRepository {

    override val file: File = fileManager.create(DISCOUNT_CONFIGURATION)

    override fun getCurrentConfiguration(): DiscountConfigurationModel {
        // Remember to prioritize the selected discount over the rest when the selector feature is added.

        return userSelectionRepository.paymentMethod?.let { pm ->
            userSelectionRepository.card?.id?.let {
                getConfiguration(configurationSolver.getConfigurationHashSelectedFor(it))
            } ?: getConfiguration(configurationSolver.getConfigurationHashSelectedFor(pm.id))
        } ?: getConfiguration(amountConfigurationRepository.value)
    }

    override fun getConfigurationSelectedFor(customOptionId: String): DiscountConfigurationModel {
        return getConfiguration(configurationSolver.getConfigurationHashSelectedFor(customOptionId))
    }

    override fun getConfigurationFor(key: PayerPaymentMethodKey): DiscountConfigurationModel {
        return getConfiguration(configurationSolver.getConfigurationHashFor(key))
    }

    private fun getConfiguration(hash: String?): DiscountConfigurationModel {
        val discountModel = value[hash]
        val defaultConfig = value[amountConfigurationRepository.value]
        return discountModel ?: defaultConfig ?: DiscountConfigurationModel.NONE
    }

    override fun readFromStorage(): Map<String, DiscountConfigurationModel> =
        fileManager.readAnyMap(file, String::class.java, DiscountConfigurationModel::class.java)
}