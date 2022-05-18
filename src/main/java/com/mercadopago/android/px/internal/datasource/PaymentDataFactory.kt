package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.internal.repository.AmountRepository
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository
import com.mercadopago.android.px.internal.repository.DiscountRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.model.Discount
import com.mercadopago.android.px.model.PaymentData

internal class PaymentDataFactory(
    private val discountRepository: DiscountRepository,
    private val userSelectionRepository: UserSelectionRepository,
    private val transactionInfoFactory: TransactionInfoFactory,
    private val amountRepository: AmountRepository,
    private val amountConfigurationRepository: AmountConfigurationRepository,
    private val paymentSettingRepository: PaymentSettingRepository
) {

    /**
     * Payment data is a dynamic non-mutable object that represents the payment state of the checkout exp.
     *
     * @return payment data at the moment is called.
     */
    fun create(): List<PaymentData> {
        val discountModel = discountRepository.getCurrentConfiguration()
        val secondaryPaymentMethodSelected = userSelectionRepository.secondaryPaymentMethod
        val paymentMethod = userSelectionRepository.paymentMethod
        val payerCost = userSelectionRepository.payerCost

        checkNotNull(paymentMethod) { "Payment method selected should not be bull" }

        val amountToPay = amountRepository.getAmountToPay(paymentMethod.paymentTypeId, payerCost)
        val transactionInfo = transactionInfoFactory.create(
            userSelectionRepository.customOptionId.orEmpty(),
            paymentMethod
        )

        if (secondaryPaymentMethodSelected != null) { // is split payment
            val currentConfiguration = amountConfigurationRepository.getCurrentConfiguration()
            val splitConfiguration = currentConfiguration.splitConfiguration
            val primaryPaymentMethod = splitConfiguration?.primaryPaymentMethod
            val secondaryPaymentMethod = splitConfiguration?.secondaryPaymentMethod

            val paymentData = PaymentData.Builder()
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setToken(paymentSettingRepository.token)
                .setIssuer(userSelectionRepository.issuer)
                .setPayer(paymentSettingRepository.checkoutPreference?.payer)
                .setTransactionInfo(transactionInfo)
                .setTransactionAmount(amountToPay)
                .setCampaign(discountModel.campaign)
                .setDiscount(primaryPaymentMethod?.discount)
                .setRawAmount(primaryPaymentMethod?.amount)
                .setNoDiscountAmount(primaryPaymentMethod?.amount)
                .createPaymentData()

            val secondaryPaymentData = PaymentData.Builder()
                .setTransactionAmount(amountToPay)
                .setPayer(paymentSettingRepository.checkoutPreference?.payer)
                .setPaymentMethod(secondaryPaymentMethodSelected)
                .setCampaign(discountModel.campaign)
                .setDiscount(secondaryPaymentMethod?.discount)
                .setRawAmount(secondaryPaymentMethod?.amount)
                .setNoDiscountAmount(secondaryPaymentMethod?.amount)
                .createPaymentData()

            return listOf(paymentData, secondaryPaymentData);
        } else { // is regular 1 pm payment

            val discount: Discount? = runCatching {
                Discount.replaceWith(
                    discountModel.discount,
                    amountConfigurationRepository.getCurrentConfiguration().discountToken
                )
            }.getOrDefault(discountModel.discount)

            val paymentData = PaymentData.Builder()
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setTransactionInfo(transactionInfo)
                .setToken(paymentSettingRepository.token)
                .setIssuer(userSelectionRepository.issuer)
                .setDiscount(discount)
                .setPayer(paymentSettingRepository.checkoutPreference?.payer)
                .setTransactionAmount(amountToPay)
                .setCampaign(discountModel.campaign)
                .setNoDiscountAmount(amountRepository.getAmountWithoutDiscount(paymentMethod.paymentTypeId,
                    payerCost))
                .setRawAmount(amountRepository.getTaxFreeAmount(paymentMethod.paymentTypeId, payerCost))
                .createPaymentData();

            return listOf(paymentData)
        }
    }
}
