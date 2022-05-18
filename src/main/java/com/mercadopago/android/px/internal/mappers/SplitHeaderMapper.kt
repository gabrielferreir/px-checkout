package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.features.one_tap.slider.SplitPaymentHeaderAdapter
import com.mercadopago.android.px.internal.features.one_tap.slider.SplitPaymentHeaderAdapter.SplitModel
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.model.internal.OneTapItem

internal class SplitHeaderMapper(private val currency: Currency,
    private val amountConfigurationRepository: AmountConfigurationRepository)
    : Mapper<OneTapItem, SplitPaymentHeaderAdapter.Model>() {

    override fun map(value: OneTapItem): SplitPaymentHeaderAdapter.Model {
        return value.takeIf { it.isCard && it.status.isEnabled }?.let { oneTapItem ->
            val cardId = oneTapItem.card.id
            val config = amountConfigurationRepository.getConfigurationSelectedFor(cardId)
            config?.takeIf { it.allowSplit() }?.let {
                val splitConfiguration = config.splitConfiguration
                    ?: throw IllegalStateException("split configuration is mandatory")
                SplitModel(currency, splitConfiguration)
            }
        } ?: SplitPaymentHeaderAdapter.Empty()
    }

}