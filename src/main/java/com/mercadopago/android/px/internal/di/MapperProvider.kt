package com.mercadopago.android.px.internal.di

import android.content.Context
import com.mercadopago.android.px.R
import com.mercadopago.android.px.addons.BehaviourProvider
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.internal.datasource.mapper.FromPayerPaymentMethodToCardMapper
import com.mercadopago.android.px.internal.features.FeatureProviderImpl
import com.mercadopago.android.px.internal.features.business_result.BusinessPaymentResultMapper
import com.mercadopago.android.px.internal.features.checkout.PostPaymentUrlsMapper
import com.mercadopago.android.px.internal.features.generic_modal.FromModalToGenericDialogItem
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModelMapper
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionActionMapper
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionInfoMapper
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionInteractionMapper
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionMapper
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionReferenceMapper
import com.mercadopago.android.px.internal.features.payment_result.mappers.PaymentResultBodyModelMapper
import com.mercadopago.android.px.internal.features.payment_result.mappers.PaymentResultViewModelMapper
import com.mercadopago.android.px.internal.features.payment_result.remedies.AlternativePayerPaymentMethodsMapper
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesLinkableMapper
import com.mercadopago.android.px.internal.features.security_code.RenderModeMapper
import com.mercadopago.android.px.internal.features.security_code.mapper.BusinessSecurityCodeDisplayDataMapper
import com.mercadopago.android.px.internal.mappers.ElementDescriptorMapper
import com.mercadopago.android.px.internal.mappers.CardDrawerCustomViewModelMapper
import com.mercadopago.android.px.internal.mappers.CardUiMapper
import com.mercadopago.android.px.internal.mappers.CustomChargeToPaymentTypeChargeMapper
import com.mercadopago.android.px.internal.mappers.InitRequestBodyMapper
import com.mercadopago.android.px.internal.mappers.OneTapItemToDisabledPaymentMethodMapper
import com.mercadopago.android.px.internal.mappers.PaymentMethodReauthMapper
import com.mercadopago.android.px.internal.mappers.PaymentMethodBehaviourDMMapper
import com.mercadopago.android.px.internal.mappers.PaymentMethodDescriptorMapper
import com.mercadopago.android.px.internal.mappers.PaymentMethodMapper
import com.mercadopago.android.px.internal.mappers.PaymentResultAmountMapper
import com.mercadopago.android.px.internal.mappers.PaymentResultMethodMapper
import com.mercadopago.android.px.internal.mappers.SummaryInfoMapper
import com.mercadopago.android.px.internal.mappers.SummaryViewModelMapper
import com.mercadopago.android.px.internal.mappers.UriToFromMapper
import com.mercadopago.android.px.internal.view.SummaryDetailDescriptorMapper
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodDrawableItemMapper
import com.mercadopago.android.px.model.internal.PaymentConfigurationMapper
import com.mercadopago.android.px.tracking.internal.mapper.FromApplicationToApplicationInfo

internal object MapperProvider {
    fun getPaymentMethodDrawableItemMapper(): PaymentMethodDrawableItemMapper {
        val session = Session.getInstance()
        return PaymentMethodDrawableItemMapper(
            session.configurationModule.chargeRepository,
            session.configurationModule.disabledPaymentMethodRepository,
            session.configurationModule.applicationSelectionRepository,
            CardUiMapper,
            CardDrawerCustomViewModelMapper,
            session.payerPaymentMethodRepository,
            session.modalRepository,
            fromModalToGenericDialogItemMapper
        )
    }

    fun getPaymentMethodDescriptorMapper(): PaymentMethodDescriptorMapper {
        val session = Session.getInstance()
        return PaymentMethodDescriptorMapper(
            session.configurationModule.paymentSettings,
            session.amountConfigurationRepository,
            session.configurationModule.disabledPaymentMethodRepository,
            session.configurationModule.applicationSelectionRepository,
            session.amountRepository
        )
    }

    fun getRenderModeMapper(context: Context): RenderModeMapper {
        with(context.resources) {
            return RenderModeMapper(configuration.screenHeightDp, getString(R.string.px_render_mode))
        }
    }

    fun getPaymentCongratsMapper(): PaymentCongratsModelMapper {
        return PaymentCongratsModelMapper(
            Session.getInstance().configurationModule.paymentSettings,
            Session.getInstance().configurationModule.trackingRepository,
            Session.getInstance().helperModule.displayInfoHelper
        )
    }

    fun getPostPaymentUrlsMapper() = PostPaymentUrlsMapper

    fun getAlternativePayerPaymentMethodsMapper(): AlternativePayerPaymentMethodsMapper {
        return AlternativePayerPaymentMethodsMapper(
            Session.getInstance().oneTapItemRepository,
            Session.getInstance().mercadoPagoESC
        )
    }

    fun getFromPayerPaymentMethodToCardMapper(): FromPayerPaymentMethodToCardMapper {
        return FromPayerPaymentMethodToCardMapper(
            Session.getInstance().oneTapItemRepository,
            Session.getInstance().payerPaymentMethodRepository,
            Session.getInstance().paymentMethodRepository
        )
    }

    fun getPaymentMethodMapper(): PaymentMethodMapper {
        return PaymentMethodMapper(Session.getInstance().paymentMethodRepository)
    }

    fun getSummaryInfoMapper(): SummaryInfoMapper {
        return SummaryInfoMapper()
    }

    fun getElementDescriptorMapper(): ElementDescriptorMapper {
        return ElementDescriptorMapper()
    }

    fun getSummaryDetailDescriptorMapper(): SummaryDetailDescriptorMapper {
        val session = Session.getInstance()
        val paymentSettings = session.configurationModule.paymentSettings
        return SummaryDetailDescriptorMapper(
            session.amountRepository,
            getSummaryInfoMapper().map(paymentSettings.checkoutPreference!!),
            FactoryProvider.amountDescriptorViewModelFactory
        )
    }

    val customChargeToPaymentTypeChargeMapper: CustomChargeToPaymentTypeChargeMapper
        get() = CustomChargeToPaymentTypeChargeMapper(
            Session.getInstance().configurationModule.paymentSettings.paymentConfiguration
        )

    fun getInitRequestBodyMapper(checkout: MercadoPagoCheckout): InitRequestBodyMapper {
        val session = Session.getInstance()
        val featureProvider = FeatureProviderImpl(checkout, BehaviourProvider.getTokenDeviceBehaviour())
        return InitRequestBodyMapper(
            session.mercadoPagoESC,
            featureProvider,
            paymentMethodsBehaviourDMMapper,
            session.configurationModule.trackingRepository
        )
    }

    fun getInitRequestBodyMapper(): InitRequestBodyMapper {
        val session = Session.getInstance()
        val featureProvider = FeatureProviderImpl(
            session.configurationModule.paymentSettings,
            BehaviourProvider.getTokenDeviceBehaviour()
        )
        return InitRequestBodyMapper(
            session.mercadoPagoESC,
            featureProvider,
            paymentMethodsBehaviourDMMapper,
            session.configurationModule.trackingRepository
        )
    }

    val paymentMethodsBehaviourDMMapper: PaymentMethodBehaviourDMMapper
        get() = PaymentMethodBehaviourDMMapper()

    val oneTapItemToDisabledPaymentMethodMapper: OneTapItemToDisabledPaymentMethodMapper
        get() = OneTapItemToDisabledPaymentMethodMapper()

    val paymentResultViewModelMapper: PaymentResultViewModelMapper
        get() {
            val session = Session.getInstance()
            val paymentSettings = session.configurationModule.paymentSettings
            return PaymentResultViewModelMapper(
                paymentSettings.advancedConfiguration.paymentResultScreenConfiguration,
                session.paymentResultViewModelFactory,
                instructionMapper,
                paymentSettings.checkoutPreference?.autoReturn,
                paymentResultBodyModelMapper
            )
        }

    val instructionMapper: InstructionMapper
        get() = InstructionMapper(
            instructionInfoMapper, instructionInteractionMapper, instructionReferenceMapper, instructionActionMapper
        )

    val instructionInfoMapper: InstructionInfoMapper
        get() = InstructionInfoMapper()

    val instructionActionMapper: InstructionActionMapper
        get() = InstructionActionMapper()

    val instructionInteractionMapper: InstructionInteractionMapper
        get() = InstructionInteractionMapper(instructionActionMapper)

    val instructionReferenceMapper: InstructionReferenceMapper
        get() = InstructionReferenceMapper()

    val fromApplicationToApplicationInfo: FromApplicationToApplicationInfo
        get() = FromApplicationToApplicationInfo()

    val fromSecurityCodeDisplayDataToBusinessSecurityCodeDisplayData: BusinessSecurityCodeDisplayDataMapper
        get() = BusinessSecurityCodeDisplayDataMapper()

    val remediesLinkableMapper: RemediesLinkableMapper
        get() = RemediesLinkableMapper(Session.getInstance().applicationContext)

    val paymentResultAmountMapper: PaymentResultAmountMapper
        get() = PaymentResultAmountMapper

    val fromModalToGenericDialogItemMapper: FromModalToGenericDialogItem
        get() = FromModalToGenericDialogItem()

    val paymentResultMethodMapper: PaymentResultMethodMapper
        get() = PaymentResultMethodMapper(Session.getInstance().applicationContext, paymentResultAmountMapper)

    val businessPaymentResultMapper: BusinessPaymentResultMapper
        get() = BusinessPaymentResultMapper(Session.getInstance().tracker, paymentResultMethodMapper)

    val paymentResultBodyModelMapper: PaymentResultBodyModelMapper
        get() {
            val session = Session.getInstance()
            val paymentSettings = session.configurationModule.paymentSettings
            return PaymentResultBodyModelMapper(
                paymentSettings.advancedConfiguration.paymentResultScreenConfiguration,
                session.tracker,
                session.helperModule.displayInfoHelper,
                paymentResultMethodMapper
            )
        }

    val paymentConfigurationMapper: PaymentConfigurationMapper
        get() {
            val session = Session.getInstance()
            val configurationModule = session.configurationModule
            return PaymentConfigurationMapper(
                session.amountConfigurationRepository,
                configurationModule.payerCostSelectionRepository,
                configurationModule.applicationSelectionRepository,
                session.customOptionIdSolver
            )
        }

    val summaryViewModelMapper: SummaryViewModelMapper
        get() {
            val session = Session.getInstance()
            val summaryInfo =
                getSummaryInfoMapper().map(session.configurationModule.paymentSettings.checkoutPreference!!)
            val elementDescriptorModel = getElementDescriptorMapper().map(summaryInfo)
            val configurationModule = session.configurationModule
            return SummaryViewModelMapper(
                session.amountRepository,
                configurationModule.chargeRepository,
                session.discountRepository,
                elementDescriptorModel,
                session.amountConfigurationRepository,
                FactoryProvider.amountDescriptorViewModelFactory,
                configurationModule.customTextsRepository,
                getSummaryDetailDescriptorMapper()
            )
        }

    val uriToFromMapper: UriToFromMapper
        get() = UriToFromMapper()
    val paymentMethodReauthMapper: PaymentMethodReauthMapper
        get() = PaymentMethodReauthMapper()
}
