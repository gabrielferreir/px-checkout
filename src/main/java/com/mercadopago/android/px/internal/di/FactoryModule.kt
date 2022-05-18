package com.mercadopago.android.px.internal.di

import com.mercadopago.android.px.internal.datasource.PaymentDataFactory
import com.mercadopago.android.px.internal.datasource.PaymentResultFactory
import com.mercadopago.android.px.internal.datasource.TransactionInfoFactory
import com.mercadopago.android.px.internal.util.SecurityValidationDataFactory

internal class FactoryModule {
    val transactionInfoFactory: TransactionInfoFactory
        get() {
            val session = Session.getInstance()
            return TransactionInfoFactory(session.payerPaymentMethodRepository)
        }


    val paymentResultFactory: PaymentResultFactory
        get() = PaymentResultFactory(paymentDataFactory)

    val paymentDataFactory: PaymentDataFactory
        get() {
            val session = Session.getInstance()
            return PaymentDataFactory(
                session.discountRepository,
                session.configurationModule.userSelectionRepository,
                transactionInfoFactory,
                session.amountRepository,
                session.amountConfigurationRepository,
                session.configurationModule.paymentSettings
            )
        }

    val securityValidationDataFactory: SecurityValidationDataFactory
        get() {
            val session = Session.getInstance()
            return SecurityValidationDataFactory(
                session.configurationModule.productIdProvider,
                session.configurationModule.paymentSettings,
                session.configurationModule.trackingRepository,
                MapperProvider.paymentMethodReauthMapper
            )
        }
}
