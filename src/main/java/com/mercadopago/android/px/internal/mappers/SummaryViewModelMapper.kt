package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.extensions.isNotNull
import com.mercadopago.android.px.internal.features.AmountDescriptorViewModelFactory
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository
import com.mercadopago.android.px.internal.repository.AmountRepository
import com.mercadopago.android.px.internal.repository.ChargeRepository
import com.mercadopago.android.px.internal.repository.CustomTextsRepository
import com.mercadopago.android.px.internal.repository.DiscountRepository
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey
import com.mercadopago.android.px.internal.util.isHighlightCharge
import com.mercadopago.android.px.internal.view.AmountDescriptorView
import com.mercadopago.android.px.internal.view.ElementDescriptorView
import com.mercadopago.android.px.internal.view.SummaryDetailDescriptorMapper
import com.mercadopago.android.px.internal.view.SummaryView
import com.mercadopago.android.px.model.AmountConfiguration
import com.mercadopago.android.px.model.DiscountConfigurationModel
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import java.math.BigDecimal

internal class SummaryViewModelMapper(
    private val amountRepository: AmountRepository,
    private val chargeRepository: ChargeRepository,
    private val discountRepository: DiscountRepository,
    private val elementDescriptorViewModel: ElementDescriptorView.Model,
    private val amountConfigurationRepository: AmountConfigurationRepository,
    private val amountDescriptorViewModelFactory: AmountDescriptorViewModelFactory,
    private val customTextsRepository: CustomTextsRepository,
    private val summaryDetailDescriptorMapper: SummaryDetailDescriptorMapper
): Mapper<CustomTotal, SummaryView.Model>() {

    private val cache = mutableMapOf<Key, SummaryView.Model>()

    var listener: AmountDescriptorView.OnClickListener? = null

    fun setAmountDescriptorListener(onClickListener: AmountDescriptorView.OnClickListener) {
        listener = onClickListener
    }

    private fun createSummaryViewModel(value: CustomTotal): SummaryView.Model {
        val discountModel = getDiscountConfiguration(value.customOptionId, value.paymentTypeId)
        val payerCost = getTotalAmountToPay(value, discountModel)
        val chargeRule = chargeRepository.getChargeRule(value.paymentTypeId)
        val amountConfiguration = getAmountConfiguration(value.customOptionId, value.paymentTypeId)
        val summaryDetailList = summaryDetailDescriptorMapper.map(
            SummaryDetailDescriptorMapper.Model(discountModel, chargeRule, amountConfiguration, listener!!)
        )
        val totalUpdated: AmountDescriptorView.Model = amountDescriptorViewModelFactory.create(
            customTextsRepository,
            payerCost)
        return SummaryView.Model(elementDescriptorViewModel, summaryDetailList, totalUpdated)
    }

    private fun mapWithCache(value: CustomTotal): SummaryView.Model {
        val key = getKey(value)
        return if (cache.containsKey(key)) {
            cache[key]!!
        } else {
            createSummaryViewModel(value).also { cache[key] = it }
        }
    }

    override fun map(value: CustomTotal): SummaryView.Model {
        return mapWithCache(value)
    }

    private fun getTotalAmountToPay(value: CustomTotal, discountModel: DiscountConfigurationModel): BigDecimal {
        val hasInstallmentOrSplit = if (PaymentTypes.isAccountMoney(value.customOptionId) || value.selectedPayerCostIndex == null)
            false
        else
            value.selectedPayerCostIndex >= 0
        return if (hasInstallmentOrSplit && value.selectedPayerCostIndex.isNotNull() && value.amountConfiguration.isNotNull() && value.selectedPayerCostIndex > 0
            && !PaymentTypes.isAccountMoney(value.customOptionId)) {
            value.amountConfiguration.getCurrentPayerCost(value.isSplitChecked, value.selectedPayerCostIndex).totalAmount
        } else {
            amountRepository.getAmountToPay(value.paymentTypeId, discountModel)
        }
    }

    private fun getDiscountConfiguration(
        customOptionId: String,
        paymentMethodTypeId: String): DiscountConfigurationModel {
        return discountRepository.getConfigurationFor(
            PayerPaymentMethodKey(customOptionId, paymentMethodTypeId)
        )
    }

    private fun getAmountConfiguration(
        customOptionId: String,
        paymentMethodTypeId: String): AmountConfiguration? {
        return amountConfigurationRepository.getConfigurationFor(
            PayerPaymentMethodKey(customOptionId, paymentMethodTypeId))
    }

    private fun getKey(value: CustomTotal): Key {
        val chargeRule = chargeRepository.getChargeRule(value.paymentTypeId)
        val discountModel = getDiscountConfiguration(
            value.customOptionId,
            value.paymentTypeId
        )
        val payerCost = getTotalAmountToPay(value, discountModel)
        val amountConfiguration: AmountConfiguration? = getAmountConfiguration(value.customOptionId, value.paymentTypeId)
        val hasSplit = amountConfiguration != null && amountConfiguration.allowSplit()
        return Key(
            discountConfigurationModel = discountModel,
            paymentTypeChargeRule = chargeRule,
            payerCost = payerCost,
            hasSplit = hasSplit
        )
    }

}

internal class CustomTotal(
    val customOptionId: String,
    val paymentTypeId: String,
    val amountConfiguration: AmountConfiguration?,
    val isSplitChecked: Boolean = false,
    val selectedPayerCostIndex: Int?
)

internal class Key(
    discountConfigurationModel: DiscountConfigurationModel,
    paymentTypeChargeRule: PaymentTypeChargeRule?,
    payerCost: BigDecimal,
    hasSplit: Boolean
) {
    private val discountConfigurationModel: DiscountConfigurationModel?
    private val paymentTypeChargeRule: PaymentTypeChargeRule?
    private val payerCost: BigDecimal
    private val hasSplit: Boolean?
    override fun hashCode(): Int {
        return (discountConfigurationModel?.hashCode() ?: 0) xor
                (paymentTypeChargeRule?.hashCode() ?: 0) xor
                payerCost.hashCode() xor
                (hasSplit?.hashCode() ?: 0)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Key) {
            return false
        }
        return (other.discountConfigurationModel == discountConfigurationModel
                && other.paymentTypeChargeRule == paymentTypeChargeRule
                && other.payerCost == payerCost
                && other.hasSplit == hasSplit)
    }

    init {
        this.discountConfigurationModel = discountConfigurationModel
        this.paymentTypeChargeRule = if (paymentTypeChargeRule != null && paymentTypeChargeRule.isHighlightCharge())
            null else paymentTypeChargeRule
        this.payerCost = payerCost
        this.hasSplit = hasSplit
    }
}
