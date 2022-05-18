package com.mercadopago.android.px.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mercadopago.android.px.internal.base.FragmentCommunicationViewModel
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.SelectorConfirmButtonViewModel
import com.mercadopago.android.px.internal.features.one_tap.offline_methods.OfflineMethodsViewModel
import com.mercadopago.android.px.internal.features.pay_button.PayButtonViewModel
import com.mercadopago.android.px.internal.features.payment_congrats.CongratsViewModel
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesViewModel
import com.mercadopago.android.px.internal.features.security_code.SecurityCodeViewModel
import com.mercadopago.android.px.internal.features.security_code.mapper.TrackingParamModelMapper
import com.mercadopago.android.px.internal.mappers.CardUiMapper
import com.mercadopago.android.px.internal.mappers.PayButtonViewModelMapper

internal class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val session = Session.getInstance()
        val configurationModule = session.configurationModule
        val paymentSetting = configurationModule.paymentSettings
        val useCaseModule = session.useCaseModule

        return when {
            modelClass.isAssignableFrom(PayButtonViewModel::class.java) -> {
                PayButtonViewModel(
                    session.congratsResultFactory,
                    session.paymentRepository,
                    session.networkModule.connectionHelper,
                    paymentSetting,
                    configurationModule.customTextsRepository,
                    PayButtonViewModelMapper(),
                    MapperProvider.getPostPaymentUrlsMapper(),
                    useCaseModule.selectPaymentSoundUseCase,
                    useCaseModule.userSelectionUseCase,
                    session.paymentResultViewModelFactory,
                    session.factoryModule.paymentDataFactory,
                    session.audioPlayer,
                    session.factoryModule.securityValidationDataFactory,
                    session.tracker
                )
            }
            modelClass.isAssignableFrom(OfflineMethodsViewModel::class.java) -> {
                OfflineMethodsViewModel(
                    paymentSetting,
                    session.amountRepository,
                    session.discountRepository,
                    session.oneTapItemRepository,
                    session.configurationModule.payerComplianceRepository,
                    session.configurationModule.flowConfigurationProvider,
                    session.tracker
                )
            }
            modelClass.isAssignableFrom(SecurityCodeViewModel::class.java) -> {
                SecurityCodeViewModel(
                    useCaseModule.tokenizeWithCvvUseCase,
                    useCaseModule.displayDataUseCase,
                    useCaseModule.securityTrackModelUseCase,
                    TrackingParamModelMapper(),
                    CardUiMapper,
                    session.configurationModule.flowConfigurationProvider,
                    session.tracker
                )
            }
            modelClass.isAssignableFrom(FragmentCommunicationViewModel::class.java) -> {
                FragmentCommunicationViewModel(session.tracker)
            }
            modelClass.isAssignableFrom(CongratsViewModel::class.java) -> {
                CongratsViewModel(
                    session.congratsRepository,
                    session.paymentRepository,
                    session.congratsResultFactory,
                    session.networkModule.connectionHelper,
                    paymentSetting,
                    MapperProvider.getPostPaymentUrlsMapper(),
                    session.factoryModule.paymentResultFactory,
                    session.tracker
                )
            }

            modelClass.isAssignableFrom(RemediesViewModel::class.java) -> {
                RemediesViewModel(session.paymentRepository,
                    session.amountConfigurationRepository,
                    session.configurationModule.applicationSelectionRepository,
                    session.useCaseModule.tokenizeWithPaymentRecoveryUseCase,
                    session.oneTapItemRepository,
                    MapperProvider.getFromPayerPaymentMethodToCardMapper(),
                    session.useCaseModule.tokenizeWithEscUseCase,
                    session.useCaseModule.tokenizeWithCvvUseCase,
                    session.tracker
                )
            }
            modelClass.isAssignableFrom(SelectorConfirmButtonViewModel::class.java) -> {
                SelectorConfirmButtonViewModel(
                    session.configurationModule.paymentSettings,
                    session.useCaseModule.userSelectionUseCase,
                    session.useCaseModule.validationProgramUseCase,
                    session.factoryModule.paymentDataFactory,
                    session.networkModule.connectionHelper,
                    session.configurationModule.customTextsRepository,
                    PayButtonViewModelMapper(),
                    session.tracker
                )
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        } as T
    }
}
