package com.mercadopago.android.px.internal.features.payment_congrats.model

import android.os.Parcelable
import java.math.BigDecimal
import kotlinx.android.parcel.Parcelize

@Parcelize
class PaymentInfo internal constructor(
    @Deprecated("") val paymentMethodName: String,
    @Deprecated("") val lastFourDigits: String?,
    @Deprecated("") val paymentMethodType: PaymentMethodType,
    @Deprecated("") val description: PaymentCongratsText?,
    @Deprecated("") val consumerCreditsInfo: PaymentResultInfo?,
    val rawAmount: String,
    val iconUrl: String?,
    val paidAmount: String,
    val discountName: String?,
    val installmentsCount: Int,
    val installmentsAmount: String?,
    val installmentsTotalAmount: String?,
    val installmentsRate: BigDecimal?,
    val details: List<PaymentCongratsText>? = null,
    val extraInfo: List<PaymentCongratsText>? = null
) : Parcelable {

    internal constructor(builder: Builder) : this(
        builder.paymentMethodName.orEmpty(),
        builder.lastFourDigits,
        builder.paymentMethodType ?: PaymentMethodType.OTHER,
        builder.description,
        builder.consumerCreditsInfo,
        builder.rawAmount.orEmpty(),
        builder.iconUrl,
        builder.paidAmount.orEmpty(),
        builder.discountName,
        builder.installmentsCount,
        builder.installmentsAmount,
        builder.installmentsTotalAmount,
        builder.installmentsRate,
        builder.details,
        builder.extraInfo
    )

    enum class PaymentMethodType(val value: String) {
        CREDIT_CARD("credit_card"),
        DEBIT_CARD("debit_card"),
        PREPAID_CARD("prepaid_card"),
        TICKET("ticket"),
        ATM("atm"),
        DIGITAL_CURRENCY("digital_currency"),
        BANK_TRANSFER("bank_transfer"),
        ACCOUNT_MONEY("account_money"),
        PLUGIN("payment_method_plugin"),
        CONSUMER_CREDITS("consumer_credits"),
        OTHER("other");

        companion object {
            @JvmStatic
            fun fromName(name: String): PaymentMethodType {
                for (paymentMethodType in values()) {
                    if (paymentMethodType.name.equals(name, ignoreCase = true)) {
                        return paymentMethodType
                    }
                }
                return OTHER
            }
        }
    }

    class Builder {
        @Deprecated("")
        var paymentMethodName: String? = ""
        @Deprecated("")
        var lastFourDigits: String? = null
        @Deprecated("")
        var paymentMethodType: PaymentMethodType? = null
        @Deprecated("")
        var description: PaymentCongratsText? = null
        @Deprecated("")
        var consumerCreditsInfo: PaymentResultInfo? = null

        var rawAmount: String? = null
        var paidAmount: String? = null
        var discountName: String? = null
        var installmentsCount = 0
        var installmentsAmount: String? = null
        var installmentsTotalAmount: String? = null
        var installmentsRate: BigDecimal? = null
        var iconUrl: String? = null
        var details: List<PaymentCongratsText>? = null
        var extraInfo: List<PaymentCongratsText>? = null

        /**
         * Instantiates a PaymentInfo object
         *
         * @return PaymentInfo
         */
        fun build() = PaymentInfo(this)

        /**
         * Adds the name of the payment method used to pay
         *
         * @param paymentMethodName the name of the payment method
         * @return Builder
         */
        @Deprecated("")
        fun withPaymentMethodName(paymentMethodName: String?) = apply {
            this.paymentMethodName = paymentMethodName
        }

        /**
         * Adds the lastFourDigits of the credit/debit card
         *
         * @param lastFourDigits the last 4 digits of the credit or debit card number
         * @return Builder
         */
        @Deprecated("")
        fun withLastFourDigits(lastFourDigits: String?) = apply {
            this.lastFourDigits = lastFourDigits
        }

        /**
         * Adds the type of the payment method used to pay
         *
         * @param paymentMethodType the type of payment method (account money, credit card, debit card, etc)
         * @return Builder
         */
        @Deprecated("")
        fun withPaymentMethodType(paymentMethodType: PaymentMethodType?) = apply {
            this.paymentMethodType = paymentMethodType
        }

        /**
         * Adds info to be displayed about consumerCredits (e.g. the date when the payer will start paying the credit
         * installments)
         *
         * @param consumerCreditsInfo info shown related to consumerCredits
         * @return Builder
         */
        @Deprecated("")
        fun withConsumerCreditsInfo(consumerCreditsInfo: PaymentResultInfo?) = apply {
            this.consumerCreditsInfo = consumerCreditsInfo
        }

        @Deprecated("")
        fun withDescription(description: PaymentCongratsText?) = apply {
            this.description = description
        }

        /**
         * Adds the url of the payment method's icon used to pay
         *
         * @param iconUrl the url of the payment method's icon
         * @return Builder
         */
        fun withIconUrl(iconUrl: String?) = apply {
            this.iconUrl = iconUrl
        }

        /**
         * Adds the total amount actually paid by the user
         *
         * @param paidAmount the amount actually paid by the user
         * @return Builder
         */
        fun withPaidAmount(paidAmount: String?) = apply {
            this.paidAmount = paidAmount
        }

        /**
         * Adds the name of the discount to be displayed (e.g.: 20% OFF)
         *
         * @param discountName the text to be displayed showing the discount (e.g.: 20% OFF)
         * @param rawAmount the value of the raw Amount for the payment without discount
         * @return Builder
         */
        fun withDiscountData(discountName: String?, rawAmount: String?) = apply {
            this.discountName = discountName
            this.rawAmount = rawAmount
        }

        /**
         * Adds the installments info
         *
         * @param installmentsCount number of installments, if there ara non 0 should be passes as param
         * @param installmentsAmount the amount to be paid for each installment
         * @param installmentsTotalAmount the total amount to pay
         * @param installmentsRate the rate/interest of the installments. If its without a rate or interest "0" should
         * @return
         */
        fun withInstallmentsData(
            installmentsCount: Int?, installmentsAmount: String?,
            installmentsTotalAmount: String?, installmentsRate: BigDecimal?
        ) = apply {
            installmentsCount?.let {
                this.installmentsCount = it
            }
            this.installmentsAmount = installmentsAmount
            this.installmentsTotalAmount = installmentsTotalAmount
            this.installmentsRate = installmentsRate
        }

        /**
         * Adds extra info to be displayed below the PaymentMethod (e.g. regulations info)
         *
         * @param extraInfo extra info shown
         * @return Builder
         */
        fun withExtraInfo(extraInfo: List<PaymentCongratsText>) = apply {
            this.extraInfo = extraInfo
        }

        fun withDetails(details: List<PaymentCongratsText>) = apply {
            this.details = details
        }
    }
}
