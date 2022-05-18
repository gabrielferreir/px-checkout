package com.mercadopago.android.px.internal.viewmodel.drawables

import com.mercadopago.android.px.internal.datasource.CustomOptionIdSolver
import com.mercadopago.android.px.internal.features.generic_modal.ActionType
import com.mercadopago.android.px.internal.features.generic_modal.FromModalToGenericDialogItem
import com.mercadopago.android.px.internal.features.generic_modal.GenericDialogItem
import com.mercadopago.android.px.internal.mappers.CardDrawerCustomViewModelMapper
import com.mercadopago.android.px.internal.mappers.CardUiMapper
import com.mercadopago.android.px.internal.mappers.NonNullMapper
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.internal.repository.ChargeRepository
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.ModalRepository
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.viewmodel.CardDrawerConfiguration
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem.Parameters
import com.mercadopago.android.px.model.AccountMoneyMetadata
import com.mercadopago.android.px.model.CardMetadata
import com.mercadopago.android.px.model.CustomSearchItem
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.BankTransfer
import com.mercadopago.android.px.model.internal.OfflineMethodCard
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.model.internal.Text
import com.mercadopago.android.px.model.one_tap.CheckoutBehaviour
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey as Key
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentCommons.ByApplication
as CommonsByApplication

internal class PaymentMethodDrawableItemMapper(
    private val chargeRepository: ChargeRepository,
    private val disabledPaymentMethodRepository: DisabledPaymentMethodRepository,
    private val applicationSelectedRepository: ApplicationSelectionRepository,
    private val cardUiMapper: CardUiMapper,
    private val cardDrawerCustomViewModelMapper: CardDrawerCustomViewModelMapper,
    private val payerPaymentMethodRepository: PayerPaymentMethodRepository,
    private val modalRepository: ModalRepository,
    private val fromModalToGenericDialogItem: FromModalToGenericDialogItem
) : NonNullMapper<OneTapItem, DrawableFragmentItem?>() {

    override fun map(value: OneTapItem): DrawableFragmentItem? {
        val parameters = getParameters(value, payerPaymentMethodRepository.value)
        with(value) {
            return when {
                isCard ||
                    isAccountMoney ||
                    isOfflineMethodCard() ||
                    isBankTransfer() -> DrawableFragmentItem(parameters)
                isConsumerCredits -> ConsumerCreditsDrawableFragmentItem(parameters, consumerCredits, displayInfo?.tag)
                isNewCard || isOfflineMethods -> OtherPaymentMethodFragmentItem(parameters, newCard, offlineMethods)
                else -> null
            }
        }
    }

    private fun getCardDrawerConfiguration(
        accountMoneyMetadata: AccountMoneyMetadata?,
        cardMetadata: CardMetadata?,
        offlineMethodCard: OfflineMethodCard?,
        paymentMethod: Application.PaymentMethod,
        cardTag: Text?,
        bankTransfer: BankTransfer?
    ): CardDrawerConfiguration? {
        return when {
            PaymentTypes.isAccountMoney(paymentMethod.type) ->
                accountMoneyMetadata?.displayInfo?.let { cardUiMapper.map(it, cardTag) } to null
            PaymentTypes.isCardPaymentType(paymentMethod.type) ->
                cardMetadata?.displayInfo?.let { cardUiMapper.map(it, cardTag) } to null
            offlineMethodCard != null -> null to cardUiMapper.map(offlineMethodCard.displayInfo, cardTag)
            bankTransfer != null -> null to cardUiMapper.map(bankTransfer.displayInfo, cardTag)
            else -> null
        }?.let {
            CardDrawerConfiguration(paymentMethod.id, it.first, it.second)
        }
    }

    private fun getParameters(
        oneTapItem: OneTapItem,
        customSearchItems: List<CustomSearchItem>
    ): Parameters {
        val displayInfo = oneTapItem.displayInfo
        val paymentMethodType = applicationSelectedRepository[oneTapItem].paymentMethod.type
        val defaultBehaviour = oneTapItem.getBehaviour(CheckoutBehaviour.Type.TAP_CARD)
        val commonsByApplication = CommonsByApplication(paymentMethodType).also {
            oneTapItem.getApplications().forEach { application ->
                val customOptionIdByApplication = CustomOptionIdSolver.getByApplication(oneTapItem, application)
                val (description, issuerName) = customSearchItems.firstOrNull { c ->
                    c.id == customOptionIdByApplication
                }?.let {
                    Pair(it.description.orEmpty(), it.issuer?.name.orEmpty())
                } ?: Pair(TextUtil.EMPTY, TextUtil.EMPTY)

                val paymentTypeId = application.paymentMethod.type
                it[application] = DrawableFragmentCommons(
                    customOptionIdByApplication,
                    application.status,
                    chargeRepository.getChargeRule(paymentTypeId)?.message,
                    disabledPaymentMethodRepository[Key(customOptionIdByApplication, paymentTypeId)],
                    buildDialogByApplication(application, defaultBehaviour),
                    description,
                    issuerName,
                    getCardDrawerConfiguration(oneTapItem.accountMoney,
                        oneTapItem.card,
                        oneTapItem.offlineMethodCard,
                        application.paymentMethod,
                        oneTapItem.displayInfo?.tag,
                        oneTapItem.bankTransfer)
                )
            }
        }

        return Parameters(
            commonsByApplication,
            displayInfo?.bottomDescription,
            oneTapItem.benefits?.reimbursement,
            cardDrawerCustomViewModelMapper.mapToSwitchModel(displayInfo?.cardDrawerSwitch, paymentMethodType)
        )
    }

    private fun buildDialogByApplication(
        application: Application,
        defaultBehaviour: CheckoutBehaviour?
    ): GenericDialogItem? {
        val modalKey = (application.behaviours[CheckoutBehaviour.Type.TAP_CARD] ?: defaultBehaviour)?.modal
        return modalKey
            ?.let { modalRepository.value[it] }
            ?.let { FromModalToGenericDialogItem.Params(ActionType.DISMISS, modalKey, it) }
            ?.let(fromModalToGenericDialogItem::map)
    }
}
