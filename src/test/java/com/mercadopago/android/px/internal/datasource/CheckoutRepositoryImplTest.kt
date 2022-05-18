package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.core.v2.PaymentProcessor
import com.mercadopago.android.px.internal.adapters.NetworkApi
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.mappers.CustomChargeToPaymentTypeChargeMapper
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.mappers.InitRequestBodyMapper
import com.mercadopago.android.px.internal.mappers.OneTapItemToDisabledPaymentMethodMapper
import com.mercadopago.android.px.internal.repository.*
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.internal.util.ApiUtil
import com.mercadopago.android.px.mocks.CheckoutResponseStub
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule.Companion.createChargeFreeRule
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.preferences.CheckoutPreference
import com.mercadopago.android.px.tracking.internal.MPTracker
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.util.*
import retrofit2.Response as RetrofitResponse

@RunWith(MockitoJUnitRunner::class)
class CheckoutRepositoryImplTest {

    private lateinit var checkoutRepository: CheckoutRepository

    @Mock
    private lateinit var paymentSettingRepository: PaymentSettingRepository

    @Mock
    private lateinit var paymentConfiguration: PaymentConfiguration

    @Mock
    private lateinit var experimentsRepository: ExperimentsRepository

    @Mock
    private lateinit var disabledPaymentMethodRepository: DisabledPaymentMethodRepository

    @Mock
    private lateinit var networkApi: NetworkApi

    @Mock
    private lateinit var tracker: MPTracker

    @Mock
    private lateinit var payerPaymentMethodRepository: PayerPaymentMethodRepository

    @Mock
    private lateinit var oneTapItemRepository: OneTapItemRepository

    @Mock
    private lateinit var paymentMethodRepository: PaymentMethodRepository

    @Mock
    private lateinit var modalRepository: ModalRepository

    @Mock
    private lateinit var payerComplianceRepository: PayerComplianceRepository

    @Mock
    private lateinit var amountConfigurationRepository: AmountConfigurationRepository

    @Mock
    private lateinit var discountRepository: DiscountRepository

    @Mock
    private lateinit var customChargeToPaymentTypeChargeMapper: CustomChargeToPaymentTypeChargeMapper

    @Mock
    private lateinit var splitPaymentProcessor: PaymentProcessor

    @Mock
    private lateinit var checkoutResponse: CheckoutResponse

    @Mock
    private lateinit var initRequestBodyMapper: InitRequestBodyMapper

    @Mock
    private lateinit var oneTapItemToDisabledPaymentMethodMapper: OneTapItemToDisabledPaymentMethodMapper

    @Mock
    private lateinit var chargesRepository: ChargeRepository

    @Before
    fun setUp() {
        whenever(paymentSettingRepository.checkoutPreferenceId).thenReturn("123456789")

        val chargeRules = ArrayList<PaymentTypeChargeRule>()
        chargeRules.add(PaymentTypeChargeRule(PaymentTypes.DIGITAL_CURRENCY, BigDecimal.TEN))
        chargeRules.add(createChargeFreeRule(PaymentTypes.ACCOUNT_MONEY, "account money"))

        paymentConfiguration = PaymentConfiguration.Builder(
            splitPaymentProcessor
        ).addChargeRules(chargeRules)
            .build()

        checkoutRepository = CheckoutRepositoryImpl(
            paymentSettingRepository,
            experimentsRepository,
            disabledPaymentMethodRepository,
            networkApi,
            tracker,
            payerPaymentMethodRepository,
            oneTapItemRepository,
            paymentMethodRepository,
            modalRepository,
            payerComplianceRepository,
            amountConfigurationRepository,
            discountRepository,
            chargesRepository,
            customChargeToPaymentTypeChargeMapper,
            initRequestBodyMapper,
            oneTapItemToDisabledPaymentMethodMapper
        )
    }

    @Test
    fun testCheckoutWithPreferenceIdResultSuccess() {
        runBlocking {
            val checkoutResponse = CheckoutResponseStub.FULL.get()
            val apiResponse = ApiResponse.Success(checkoutResponse)

            whenever(
                networkApi.apiCallForResponse(
                    any(), any<suspend (api: CheckoutService) -> RetrofitResponse<CheckoutResponse>>()
                )
            ).thenReturn(apiResponse)
            val response = checkoutRepository.checkout()
            val expectedResult = Response.Success(apiResponse.result)
            assertTrue(ReflectionEquals(expectedResult).matches(response))
        }
    }

    @Test
    fun testCheckoutWithPreferenceIdResultFailure() {
        val apiExceptionMsg = "test message"
        val apiException = ApiException().apply { message = apiExceptionMsg }

        runBlocking {
            val apiResponse = ApiResponse.Failure(apiException)
            whenever(
                networkApi.apiCallForResponse(
                    any(), any<suspend (api: CheckoutService) -> RetrofitResponse<CheckoutResponse>>()
                )
            ).thenReturn(apiResponse)
            val response = checkoutRepository.checkout() as Response.Failure
            val expectedResult =
                Response.Failure(MercadoPagoError(apiResponse.exception, ApiUtil.RequestOrigin.POST_INIT))
            assertTrue(ReflectionEquals(expectedResult.exception).matches(response.exception))
        }
    }

    @Test
    fun testCheckoutWithoutPreferenceIdResultSuccess() {
        runBlocking {
            val checkoutResponse = CheckoutResponseStub.FULL.get()
            val apiResponse = ApiResponse.Success(checkoutResponse)
            whenever(
                networkApi.apiCallForResponse(
                    any(), any<suspend (api: CheckoutService) -> RetrofitResponse<CheckoutResponse>>()
                )
            ).thenReturn(apiResponse)
            val response = checkoutRepository.checkout()
            val expectedResult = Response.Success(apiResponse.result)
            assertTrue(ReflectionEquals(expectedResult).matches(response))
        }
    }

    @Test
    fun testCheckoutWithoutPreferenceIdFailure() {
        val apiExceptionMsg = "test message"
        val apiException = ApiException().apply { message = apiExceptionMsg }

        runBlocking {
            val apiResponse = ApiResponse.Failure(apiException)
            whenever(
                networkApi.apiCallForResponse(
                    any(), any<suspend (api: CheckoutService) -> RetrofitResponse<CheckoutResponse>>()
                )
            ).thenReturn(apiResponse)
            val response = checkoutRepository.checkout() as Response.Failure
            val expectedResult =
                Response.Failure(MercadoPagoError(apiResponse.exception, ApiUtil.RequestOrigin.POST_INIT))
            assertTrue(ReflectionEquals(expectedResult.exception).matches(response.exception))
        }
    }

    @Test
    fun testConfigureWithoutPreferenceId() {
        runBlocking {
            checkoutRepository.configure(mock())

            verify(paymentSettingRepository).configure(checkoutResponse.site)
            verify(paymentSettingRepository).configure(checkoutResponse.currency)
            verify(paymentSettingRepository).configure(checkoutResponse.configuration)
            verify(experimentsRepository).configure(checkoutResponse.experiments)
            verify(payerPaymentMethodRepository).configure(checkoutResponse.payerPaymentMethods)
            verify(oneTapItemRepository).configure(checkoutResponse.oneTapItems)
            verify(paymentMethodRepository).configure(checkoutResponse.availablePaymentMethods)
            verify(modalRepository).configure(checkoutResponse.modals)
            verify(payerComplianceRepository).configure(checkoutResponse.payerCompliance)
            verify(amountConfigurationRepository).configure(checkoutResponse.defaultAmountConfiguration)
            verify(discountRepository).configure(checkoutResponse.discountsConfigurations)
            verify(disabledPaymentMethodRepository).configure(OneTapItemToDisabledPaymentMethodMapper().map(checkoutResponse.oneTapItems))
            verify(tracker).setExperiments(experimentsRepository.experiments)
        }
    }

    @Test
    fun testConfigureWithPreferenceId() {
        runBlocking {
            whenever(checkoutResponse.preference).thenReturn(Mockito.mock(CheckoutPreference::class.java))
            checkoutRepository.configure(checkoutResponse)

            verify(paymentSettingRepository).configure(checkoutResponse.preference)
            verify(paymentSettingRepository).configure(checkoutResponse.site)
            verify(paymentSettingRepository).configure(checkoutResponse.currency)
            verify(paymentSettingRepository).configure(checkoutResponse.configuration)
            verify(customChargeToPaymentTypeChargeMapper).map(checkoutResponse.customCharges ?: mapOf())
            verify(chargesRepository).configure(anyList())
            verify(experimentsRepository).configure(checkoutResponse.experiments)
            verify(payerPaymentMethodRepository).configure(checkoutResponse.payerPaymentMethods)
            verify(oneTapItemRepository).configure(checkoutResponse.oneTapItems)
            verify(paymentMethodRepository).configure(checkoutResponse.availablePaymentMethods)
            verify(modalRepository).configure(checkoutResponse.modals)
            verify(payerComplianceRepository).configure(checkoutResponse.payerCompliance)
            verify(amountConfigurationRepository).configure(checkoutResponse.defaultAmountConfiguration)
            verify(discountRepository).configure(checkoutResponse.discountsConfigurations)
            verify(disabledPaymentMethodRepository).configure(OneTapItemToDisabledPaymentMethodMapper().map(checkoutResponse.oneTapItems))
            verify(tracker).setExperiments(experimentsRepository.experiments)
        }
    }

    @Test
    fun whenApiResponseHaveCardWithRetryAndOnSecondCallNoRetryNeededThenItShouldRetryAndReturnSuccess() {
        val checkoutResponse = CheckoutResponseStub.ONE_TAP_VISA_CREDIT_CARD.get()
        val retryCheckoutResponse = CheckoutResponseStub.ONE_TAP_CREDIT_CARD_WITH_RETRY.get()
        val cardFoundWithRetryId = checkoutResponse.oneTapItems.first().card.id

        val response = runBlocking {
            whenever(
                networkApi.apiCallForResponse(
                    any(), any<suspend (api: CheckoutService) -> RetrofitResponse<CheckoutResponse>>()
                )
            ).thenReturn(
                ApiResponse.Success(retryCheckoutResponse), ApiResponse.Success(checkoutResponse)
            )
            checkoutRepository.checkoutWithNewCard(cardFoundWithRetryId)
        }
        runBlocking {
            verify(networkApi, times(2)).apiCallForResponse(
                any(), any<suspend (api: CheckoutService) -> RetrofitResponse<CheckoutResponse>>()
            )
        }
        assertTrue(response is Response.Success)
        with(response as Response.Success) {
            assertTrue(ReflectionEquals(checkoutResponse).matches(this.result))
        }
    }

    @Test
    fun whenApiResponseHaveCardWithRetryAndOnSubsequentCallsApiFailsItShouldReturnSuccessWithCheckoutResponse() {
        val retryCheckoutResponse = CheckoutResponseStub.ONE_TAP_CREDIT_CARD_WITH_RETRY.get()
        val cardFoundWithRetryId = retryCheckoutResponse.oneTapItems.first().card.id
        val exMsg = "Test exception msg"

        val response = runBlocking {
            whenever(
                networkApi.apiCallForResponse(
                    any(), any<suspend (api: CheckoutService) -> RetrofitResponse<CheckoutResponse>>()
                )
            ).thenReturn(
                ApiResponse.Success(retryCheckoutResponse),
                ApiResponse.Failure(ApiException().apply { message = exMsg })
            )
            checkoutRepository.checkoutWithNewCard(cardFoundWithRetryId)
        }

        runBlocking {
            verify(networkApi, atLeast(2)).apiCallForResponse(
                any(), any<suspend (api: CheckoutService) -> RetrofitResponse<CheckoutResponse>>()
            )
        }
        assertTrue(response is Response.Success)
        with(response as Response.Success) {
            assertTrue(ReflectionEquals(retryCheckoutResponse).matches(this.result))
        }
    }
}
