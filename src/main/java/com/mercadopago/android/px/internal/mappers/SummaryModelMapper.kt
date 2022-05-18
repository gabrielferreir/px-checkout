package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.datasource.CustomOptionIdSolver
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.internal.view.SummaryView
import com.mercadopago.android.px.internal.viewmodel.SummaryModel
import com.mercadopago.android.px.model.internal.OneTapItem

internal class SummaryModelMapper(
    val amountConfigurationRepository: AmountConfigurationRepository,
    private val applicationSelectionRepository: ApplicationSelectionRepository,
    private val summaryViewModelMapper: SummaryViewModelMapper
) : Mapper<OneTapItem, SummaryModel>() {

    override fun map(values: Iterable<OneTapItem>) = mutableListOf<SummaryModel>().also {
        values.forEach { value ->
            val currentPmTypeSelection = getCurrentPmTypeSelection(value)
            it.add(SummaryModel(currentPmTypeSelection, mapToSummaryViewModel(value)))
        }
    }

    override fun map(value: OneTapItem) = SummaryModel(
        value.getDefaultPaymentMethodType(),
        mapToSummaryViewModel(value)
    )

    private fun mapToSummaryViewModel(value: OneTapItem): Map<String, SummaryView.Model> {
        val map = mutableMapOf<String, SummaryView.Model>()
        value.getApplications().forEach { application ->
            val customOptionId = CustomOptionIdSolver.getByApplication(value, application)
            val paymentMethodTypeId = application.paymentMethod.type
            map[paymentMethodTypeId] = createModel(customOptionId, paymentMethodTypeId)
        }

        return map
    }

    private fun getCurrentPmTypeSelection(oneTapItem: OneTapItem): String {
        return applicationSelectionRepository[oneTapItem].paymentMethod.type
    }

    private fun createModel(customOptionId: String, paymentTypeId: String): SummaryView.Model {
        val amountConfiguration = amountConfigurationRepository.getConfigurationSelectedFor(customOptionId)
        return summaryViewModelMapper.map(
            CustomTotal(
                customOptionId = customOptionId,
                paymentTypeId = paymentTypeId,
                amountConfiguration = amountConfiguration,
                isSplitChecked = false,
                selectedPayerCostIndex = null
            )
        )
    }

}
