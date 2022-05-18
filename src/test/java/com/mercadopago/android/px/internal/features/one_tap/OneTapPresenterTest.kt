package com.mercadopago.android.px.internal.features.one_tap

import com.mercadopago.android.px.TestContextProvider
import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.configuration.DynamicDialogConfiguration
import com.mercadopago.android.px.core.DynamicDialogCreator
import com.mercadopago.android.px.core.internal.FlowConfigurationProvider
import com.mercadopago.android.px.internal.base.use_case.TokenizeWithEscUseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.core.AuthorizationProvider
import com.mercadopago.android.px.internal.datasource.CustomOptionIdSolver
import com.mercadopago.android.px.internal.domain.CheckoutUseCase
import com.mercadopago.android.px.internal.domain.CheckoutWithNewBankAccountCardUseCase
import com.mercadopago.android.px.internal.domain.CheckoutWithNewCardUseCase
import com.mercadopago.android.px.internal.features.generic_modal.FromModalToGenericDialogItem
import com.mercadopago.android.px.internal.features.pay_button.PayButtonFragment
import com.mercadopago.android.px.internal.mappers.ElementDescriptorMapper
import com.mercadopago.android.px.internal.mappers.SummaryInfoMapper
import com.mercadopago.android.px.internal.mappers.UriToFromMapper
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.internal.repository.CheckoutRepository
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.DiscountRepository
import com.mercadopago.android.px.internal.repository.ExperimentsRepository
import com.mercadopago.android.px.internal.repository.ModalRepository
import com.mercadopago.android.px.internal.repository.OneTapItemRepository
import com.mercadopago.android.px.internal.repository.PayerCostSelectionRepository
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.PaymentRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.internal.viewmodel.FlowConfigurationModel
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodDrawableItemMapper
import com.mercadopago.android.px.mocks.CurrencyStub
import com.mercadopago.android.px.mocks.SiteStub
import com.mercadopago.android.px.model.AmountConfiguration
import com.mercadopago.android.px.model.CardMetadata
import com.mercadopago.android.px.model.DiscountConfigurationModel
import com.mercadopago.android.px.model.Item
import com.mercadopago.android.px.model.PayerCost
import com.mercadopago.android.px.model.StatusMetadata
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.*
import com.mercadopago.android.px.preferences.CheckoutPreference
import com.mercadopago.android.px.tracking.internal.BankInfoHelper
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.AbortEvent
import com.mercadopago.android.px.tracking.internal.events.BackEvent
import com.mercadopago.android.px.tracking.internal.views.OneTapViewTracker
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class OneTapPresenterTest {

    private val view = mockk<OneTap.View>(relaxed = true)

    private val paymentRepository = mockk<PaymentRepository>()

    private val paymentSettingRepository = mockk<PaymentSettingRepository>()

    private val disabledPaymentMethodRepository = mockk<DisabledPaymentMethodRepository>(relaxed = true)

    private val payerCostSelectionRepository = mockk<PayerCostSelectionRepository>(relaxed = true)

    private val checkoutRepository = mockk<CheckoutRepository>()

    private val discountRepository = mockk<DiscountRepository>()

    private val amountConfigurationRepository = mockk<AmountConfigurationRepository>()

    private val oneTapItem = mockk<OneTapItem>(relaxed = true)

    private val amountConfiguration = mockk<AmountConfiguration>(relaxed = true)

    private val discountConfigurationModel = mockk<DiscountConfigurationModel>(relaxed = true) {
        every { discount } returns null
        every { campaign } returns null
    }

    private val advancedConfiguration = mockk<AdvancedConfiguration>()

    private val dynamicDialogConfiguration = mockk<DynamicDialogConfiguration>(relaxed = true)

    private val escManagerBehaviour = mockk<ESCManagerBehaviour>(relaxed = true)

    private val paymentMethodDrawableItemMapper = mockk<PaymentMethodDrawableItemMapper>(relaxed = true)

    private val experimentsRepository = mockk<ExperimentsRepository>(relaxed = true)

    private val applicationSelectionRepository = mockk<ApplicationSelectionRepository>(relaxed = true)

    private val trackingRepository = mockk<TrackingRepository>()

    private val cardMetadata = mockk<CardMetadata>()

    private val tracker = mockk<MPTracker>(relaxed = true)

    private val oneTapItemRepository = mockk<OneTapItemRepository>()

    private val payerPaymentMethodRepository = mockk<PayerPaymentMethodRepository>(relaxed = true)

    private val modalRepository = mockk<ModalRepository>()

    private val summaryInfoMapper = mockk<SummaryInfoMapper>()

    private val elementDescriptorMapper = mockk<ElementDescriptorMapper>()

    private val application = mockk<Application>()

    private val customOptionIdSolver = mockk<CustomOptionIdSolver>()

    private val authorizationProvider = mockk<AuthorizationProvider>()

    private val tokenizeWithEscUseCase = mockk<TokenizeWithEscUseCase>()

    private val paymentConfigurationMapper = mockk<PaymentConfigurationMapper>()
    private val fromModalToGenericDialogItem = mockk<FromModalToGenericDialogItem>()
    private val flowConfigurationProvider = mockk<FlowConfigurationProvider>()
    private val bankInfoHelper = mockk<BankInfoHelper>()
    private val flowConfigurationModel = mockk<FlowConfigurationModel>()
    private val uriToFromMapper = mockk<UriToFromMapper>()
    private val checkoutWithNewBankAccountCardUseCase = mockk<CheckoutWithNewBankAccountCardUseCase>()

    private lateinit var checkoutUseCase: CheckoutUseCase
    private lateinit var checkoutWithNewCardUseCase: CheckoutWithNewCardUseCase

    private lateinit var oneTapPresenter: OneTapPresenter

    @Before
    fun setUp() {
        val customOptionId = "123"
        val item = mockk<Item>(relaxed = true) {
            every { unitPrice } returns BigDecimal.TEN
        }
        val preference = mockk<CheckoutPreference>(relaxed = true) {
            every { totalAmount } returns BigDecimal.TEN
            every { items } returns listOf(item)
        }
        val applicationPaymentMethod = mockk<Application.PaymentMethod> {
            every { type } returns "credit_card"
        }

        checkoutUseCase = CheckoutUseCase(checkoutRepository, tracker, TestContextProvider())
        checkoutWithNewCardUseCase = CheckoutWithNewCardUseCase(checkoutRepository, tracker, TestContextProvider())

        every { paymentSettingRepository.site } returns SiteStub.MLA.get()
        every { paymentSettingRepository.currency } returns CurrencyStub.MLA.get()
        every { paymentSettingRepository.checkoutPreference } returns preference
        every { paymentSettingRepository.advancedConfiguration } returns advancedConfiguration
        every { advancedConfiguration.dynamicDialogConfiguration } returns dynamicDialogConfiguration
        every { oneTapItem.isCard } returns true
        every { oneTapItem.card } returns cardMetadata
        every { cardMetadata.displayInfo } returns mockk(relaxed = true)
        every { cardMetadata.id } returns customOptionId
        every { customOptionIdSolver[oneTapItem] } returns customOptionId
        every { oneTapItem.status } returns mockk(relaxed = true)
        every { discountRepository.getCurrentConfiguration() } returns discountConfigurationModel
        every { amountConfigurationRepository.getConfigurationSelectedFor(customOptionId) } returns amountConfiguration
        every { oneTapItemRepository.value } returns listOf(oneTapItem)
        every { disabledPaymentMethodRepository.value } returns hashMapOf()
        every { application.paymentMethod } returns applicationPaymentMethod
        every { applicationSelectionRepository[oneTapItem] } returns application
        every { applicationSelectionRepository[customOptionId] } returns application
        every { summaryInfoMapper.map(preference) } returns mockk()
        every { elementDescriptorMapper.map(any<SummaryInfo>()) } returns mockk()
        every { flowConfigurationModel.confirmButton } returns mockk<PayButtonFragment>()
        every { flowConfigurationProvider.getFlowConfiguration() } returns flowConfigurationModel

        oneTapPresenter = OneTapPresenter(
            paymentSettingRepository,
            disabledPaymentMethodRepository,
            payerCostSelectionRepository,
            applicationSelectionRepository,
            discountRepository,
            checkoutUseCase,
            checkoutWithNewCardUseCase,
            amountConfigurationRepository,
            escManagerBehaviour,
            experimentsRepository,
            trackingRepository,
            oneTapItemRepository,
            payerPaymentMethodRepository,
            modalRepository,
            customOptionIdSolver,
            paymentMethodDrawableItemMapper,
            mockk(relaxed = true),
            summaryInfoMapper,
            elementDescriptorMapper,
            mockk(relaxed = true),
            authorizationProvider,
            tokenizeWithEscUseCase,
            paymentConfigurationMapper,
            flowConfigurationProvider,
            bankInfoHelper,
            fromModalToGenericDialogItem,
            mockk(relaxed = true),
            uriToFromMapper,
            checkoutWithNewBankAccountCardUseCase,
            tracker
        )
        verifyAttachView()
    }

    @Test
    fun whenFailToRetrieveCheckoutThenShowError() {
        val mercadoPagoError = mockk<MercadoPagoError> {
            every { apiException } returns mockk()
        }
        coEvery { checkoutRepository.checkout() } returns Response.Failure(mercadoPagoError)

        oneTapPresenter.handleDeepLink()

        coVerify { checkoutRepository.checkout() }
        verify { view.showErrorActivity(any()) }
    }

    @Test
    fun whenBackThenTrackAbort() {
        oneTapPresenter.onFreshStart()
        oneTapPresenter.onBack()

        verify { tracker.track(any<OneTapViewTracker>()) }
        verify { tracker.track(any<AbortEvent>()) }
        confirmVerified(tracker)
    }

    @Test
    fun whenOnFreshStartThenTrackView() {
        oneTapPresenter.onFreshStart()

        verify { tracker.track(any<OneTapViewTracker>()) }
        confirmVerified(tracker)
    }

    @Test
    fun whenDirtyStartThenNotTrackView() {
        verify { tracker wasNot called }
    }

    @Test
    fun whenCanceledThenCancelAndTrack() {
        oneTapPresenter.onFreshStart()
        oneTapPresenter.cancel()

        verify { view.cancel() }
        confirmVerified(view)
        verify { tracker.track(any<OneTapViewTracker>()) }
        verify { tracker.track(any<BackEvent>()) }
        confirmVerified(tracker)
    }

    @Test
    fun whenInstallmentsRowPressedShowInstallments() {
        val selectedPayerCostIndex = 2
        every { amountConfiguration.getCurrentPayerCostIndex(any(), any()) } returns selectedPayerCostIndex

        oneTapPresenter.onInstallmentsRowPressed()

        verify { view.updateInstallmentsList(selectedPayerCostIndex, any()) }
        verify { view.animateInstallmentsList() }
        verify { tracker.track(any()) }
        confirmVerified(tracker)
        confirmVerified(view)
    }

    @Test
    fun whenInstallmentsSelectionCancelledThenCollapseInstallments() {
        val paymentMethodIndex = 0
        val splitSelectionState = mockk<SplitSelectionState>()
        val state = mockk<OneTapState>()
        every { state.paymentMethodIndex } returns paymentMethodIndex
        every { state.splitSelectionState } returns splitSelectionState
        val payerCostIndex = 2
        val customOptionId = customOptionIdSolver[oneTapItem]
        every { payerCostSelectionRepository.get(customOptionId) } returns payerCostIndex

        oneTapPresenter.restoreState(state)
        oneTapPresenter.onInstallmentSelectionCanceled()

        verify { view.updateViewForPosition(paymentMethodIndex, payerCostIndex, splitSelectionState, application) }
        verify { view.collapseInstallmentsSelection() }
    }

    @Test
    fun whenViewIsResumedThenPaymentRepositoryIsAttached() {
        confirmVerified(paymentRepository)
        confirmVerified(view)
        confirmVerified(dynamicDialogConfiguration)
    }

    @Test
    fun whenElementDescriptorViewClickedAndHasCreatorThenShowDynamicDialog() {
        val dynamicDialogCreatorMock = mockk<DynamicDialogCreator>()
        every { dynamicDialogConfiguration.hasCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER) } returns true
        every { dynamicDialogConfiguration.getCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER) } returns dynamicDialogCreatorMock

        oneTapPresenter.onHeaderClicked()

        verify { dynamicDialogConfiguration.hasCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER) }
        verify { dynamicDialogConfiguration.getCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER) }
        verify { view.showDynamicDialog(dynamicDialogCreatorMock, any()) }
        confirmVerified(view)
        confirmVerified(dynamicDialogConfiguration)
    }

    @Test
    fun whenElementDescriptorViewClickedAndHasNotCreatorThenDoNotShowDynamicDialog() {
        oneTapPresenter.onHeaderClicked()

        verify { dynamicDialogConfiguration.hasCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER) }
        confirmVerified(view)
        confirmVerified(dynamicDialogConfiguration)
    }

    @Test
    fun whenDisabledDescriptorViewClickThenShowDisabledDialog() {
        val disabledPaymentMethod = mockk<DisabledPaymentMethod>()
        val statusMetadata = mockk<StatusMetadata>()
        every { disabledPaymentMethodRepository[any()] } returns disabledPaymentMethod
        every { application.status } returns statusMetadata

        oneTapPresenter.onDisabledDescriptorViewClick()

        verify { view.showDisabledPaymentMethodDetailDialog(disabledPaymentMethod, statusMetadata) }
    }

    @Test
    fun whenSliderOptionSelectedThenShowInstallmentsRow() {
        every { payerCostSelectionRepository[any()] } returns PayerCost.NO_SELECTED
        val currentElementPosition = 0
        every { discountRepository.getConfigurationFor(any())} returns mockk()
        every { amountConfigurationRepository.getConfigurationFor(any())} returns mockk()

        oneTapPresenter.onSliderOptionSelected(currentElementPosition)

        verify { view.updateViewForPosition(currentElementPosition, PayerCost.NO_SELECTED, any(), any()) }
        verify { view.updateTotalValue(any()) }
        confirmVerified(view)
    }

    @Test
    fun whenPayerCostSelectedThenItsReflectedOnView() {
        val paymentMethodIndex = 0
        val selectedPayerCostIndex = 1
        val payerCostList = mockPayerCosts(selectedPayerCostIndex)
        val payerCostSelected = payerCostList[selectedPayerCostIndex]

        every { discountRepository.getConfigurationFor(any())} returns mockk()
        every { amountConfigurationRepository.getConfigurationFor(any())} returns mockk()
        every { payerCostSelected.totalAmount } returns BigDecimal.TEN

        oneTapPresenter.onPayerCostSelected(payerCostList[selectedPayerCostIndex])

        verify { view.updateViewForPosition(paymentMethodIndex, selectedPayerCostIndex, any(), any()) }
        verify { view.collapseInstallmentsSelection() }
        verify { view.updateTotalValue(any()) }
        confirmVerified(view)
    }

    private fun mockPayerCosts(selectedPayerCostIndex: Int): List<PayerCost> {
        every { payerCostSelectionRepository[any()] } returns selectedPayerCostIndex
        val firstPayerCost = mockk<PayerCost>()
        val payerCostList = listOf(mockk(), firstPayerCost, mockk())
        every { amountConfiguration.getAppliedPayerCost(false) } returns payerCostList
        return payerCostList
    }

    private fun verifyAttachView() {
        oneTapPresenter.attachView(view)
        verify { view.configurePayButton(any()) }
        verify { view.configurePaymentMethodHeader(any()) }
        verify { view.showHorizontalElementDescriptor(any()) }
        verify { view.updateViewForPosition(any(), any(), any(), any()) }
        verify { view.configureRenderMode(any()) }
        verify { view.configureAdapters(any()) }
        verify { view.updatePaymentMethods(any()) }
        verify { view.configureFlow(flowConfigurationModel) }
    }
}
