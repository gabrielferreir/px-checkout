package com.mercadopago.android.px.internal.di

import com.mercadopago.android.px.addons.BehaviourProvider
import com.mercadopago.android.px.internal.audio.SelectPaymentSoundUseCase
import com.mercadopago.android.px.internal.base.use_case.TokenizeWithCvvUseCase
import com.mercadopago.android.px.internal.base.use_case.TokenizeWithEscUseCase
import com.mercadopago.android.px.internal.base.use_case.TokenizeWithPaymentRecoveryUseCase
import com.mercadopago.android.px.internal.base.use_case.UserSelectionUseCase
import com.mercadopago.android.px.internal.domain.CheckoutUseCase
import com.mercadopago.android.px.internal.domain.CheckoutWithNewBankAccountCardUseCase
import com.mercadopago.android.px.internal.domain.CheckoutWithNewCardUseCase
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.DisplayDataUseCase
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.SecurityTrackModelUseCase
import com.mercadopago.android.px.internal.features.validation_program.AuthenticateUseCase
import com.mercadopago.android.px.internal.features.validation_program.ValidationProgramUseCase

internal class UseCaseModule(
    private val configurationModule: CheckoutConfigurationModule,
    private val mapperProvider: MapperProvider
) {

    val tokenizeWithEscUseCase: TokenizeWithEscUseCase
        get() {
            val session = Session.getInstance()
            return TokenizeWithEscUseCase(
                session.cardTokenRepository,
                session.mercadoPagoESC,
                session.escPaymentManager,
                configurationModule.paymentSettings,
                configurationModule.userSelectionRepository,
                session.tracker
            )
        }

    val tokenizeWithCvvUseCase: TokenizeWithCvvUseCase
        get() {
            val session = Session.getInstance()
            return TokenizeWithCvvUseCase(
                session.cardTokenRepository,
                session.mercadoPagoESC,
                configurationModule.userSelectionRepository,
                configurationModule.paymentSettings,
                session.tracker
            )
        }

    val tokenizeWithPaymentRecoveryUseCase: TokenizeWithPaymentRecoveryUseCase
        get() {
            val session = Session.getInstance()
            return TokenizeWithPaymentRecoveryUseCase(
                session.cardTokenRepository,
                session.mercadoPagoESC,
                configurationModule.paymentSettings,
                session.tracker
            )
        }

    val userSelectionUseCase: UserSelectionUseCase
        get() {
            val session = Session.getInstance()
            return UserSelectionUseCase(
                configurationModule.userSelectionRepository,
                session.amountConfigurationRepository,
                session.paymentMethodRepository,
                mapperProvider.getFromPayerPaymentMethodToCardMapper(),
                mapperProvider.getPaymentMethodMapper(),
                session.tracker
            )
        }

    val displayDataUseCase: DisplayDataUseCase
        get() {
            val session = Session.getInstance()
            return DisplayDataUseCase(
                mapperProvider.fromSecurityCodeDisplayDataToBusinessSecurityCodeDisplayData,
                session.tracker,
                session.oneTapItemRepository)
        }

    val securityTrackModelUseCase: SecurityTrackModelUseCase
        get() {
            val session = Session.getInstance()
            return SecurityTrackModelUseCase(session.tracker)
        }

    val validationProgramUseCase: ValidationProgramUseCase
        get() {
            val session = Session.getInstance()
            return ValidationProgramUseCase(
                configurationModule.applicationSelectionRepository,
                authenticateUseCase,
                session.tracker
            )
        }

    private val authenticateUseCase: AuthenticateUseCase
        get() {
            val session = Session.getInstance()
            return AuthenticateUseCase(
                session.tracker,
                BehaviourProvider.getThreeDSBehaviour(),
                session.cardHolderAuthenticationRepository
            )
        }

    val selectPaymentSoundUseCase: SelectPaymentSoundUseCase
        get() {
            val session = Session.getInstance()
            return SelectPaymentSoundUseCase(session.tracker, configurationModule.paymentSettings)
        }

    val checkoutUseCase: CheckoutUseCase
        get() {
            val session = Session.getInstance()
            return CheckoutUseCase(session.checkoutRepository, session.tracker)
        }

    val checkoutWithNewCardUseCase: CheckoutWithNewCardUseCase
        get() {
            val session = Session.getInstance()
            return CheckoutWithNewCardUseCase(session.checkoutRepository, session.tracker)
        }

    val checkoutWithNewBankAccountCardUseCase: CheckoutWithNewBankAccountCardUseCase
        get() {
            val session = Session.getInstance()
            return CheckoutWithNewBankAccountCardUseCase(session.checkoutRepository, session.tracker)
        }
}
