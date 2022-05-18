package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.adapters.NetworkApi
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.mappers.CustomChargeToPaymentTypeChargeMapper
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.mappers.InitRequestBodyMapper
import com.mercadopago.android.px.internal.mappers.OneTapItemToDisabledPaymentMethodMapper
import com.mercadopago.android.px.internal.repository.*
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.internal.util.ApiUtil
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.tracking.internal.MPTracker
import kotlinx.coroutines.delay

internal typealias ApiResponseCallback<R> = ApiResponse<R, ApiException>
internal typealias ResponseCallback<R> = Response<R, MercadoPagoError>

internal open class CheckoutRepositoryImpl(
    private val paymentSettingRepository: PaymentSettingRepository,
    private val experimentsRepository: ExperimentsRepository,
    private val disabledPaymentMethodRepository: DisabledPaymentMethodRepository,
    private val networkApi: NetworkApi,
    private val tracker: MPTracker,
    private val payerPaymentMethodRepository: PayerPaymentMethodRepository,
    private val oneTapItemRepository: OneTapItemRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val modalRepository: ModalRepository,
    private val payerComplianceRepository: PayerComplianceRepository,
    private val amountConfigurationRepository: AmountConfigurationRepository,
    private val discountRepository: DiscountRepository,
    private val chargesRepository: ChargeRepository,
    private val customChargeToPaymentTypeChargeMapper: CustomChargeToPaymentTypeChargeMapper,
    private val initRequestBodyMapper: InitRequestBodyMapper,
    private val oneTapItemToDisabledPaymentMethodMapper: OneTapItemToDisabledPaymentMethodMapper
) : CheckoutRepository {

    override suspend fun checkout() = doCheckout()

    override fun configure(checkoutResponse: CheckoutResponse) {
        if (checkoutResponse.preference != null) {
            paymentSettingRepository.configure(checkoutResponse.preference)
        }
        paymentSettingRepository.configure(checkoutResponse.site)
        paymentSettingRepository.configure(checkoutResponse.currency)
        paymentSettingRepository.configure(checkoutResponse.configuration)

        // TODO: Remove null check when backend has IDC ready
        checkoutResponse.customCharges?.let {
            chargesRepository.configure(
                customChargeToPaymentTypeChargeMapper.map(it)
            )
        }

        experimentsRepository.configure(checkoutResponse.experiments)
        payerPaymentMethodRepository.configure(checkoutResponse.payerPaymentMethods)
        oneTapItemRepository.configure(checkoutResponse.oneTapItems)
        paymentMethodRepository.configure(checkoutResponse.availablePaymentMethods)
        modalRepository.configure(checkoutResponse.modals)
        payerComplianceRepository.configure(checkoutResponse.payerCompliance)
        amountConfigurationRepository.configure(checkoutResponse.defaultAmountConfiguration)
        discountRepository.configure(checkoutResponse.discountsConfigurations)
        disabledPaymentMethodRepository.configure(
            oneTapItemToDisabledPaymentMethodMapper.map(checkoutResponse.oneTapItems)
        )
        tracker.setExperiments(experimentsRepository.experiments)
    }

    override suspend fun checkoutWithNewCard(cardId: String): ResponseCallback<CheckoutResponse> {
        return updateCheckout(cardId = cardId)
    }

    private suspend fun updateCheckout(cardId: String? = null, bankAccountId: String? = null): ResponseCallback<CheckoutResponse> {
        var retriesAvailable = MAX_REFRESH_RETRIES
        var findPaymentMethodResult = checkoutAndFindPaymentMethod(cardId, bankAccountId)
        var lastSuccessResponse = findPaymentMethodResult.response.takeIf { it is Response.Success }
        while (findPaymentMethodResult.retryNeeded && retriesAvailable > 0) {
            retriesAvailable--
            delay(RETRY_DELAY)
            findPaymentMethodResult = checkoutAndFindPaymentMethod(cardId, bankAccountId)
            if (findPaymentMethodResult.response is Response.Success) {
                lastSuccessResponse = findPaymentMethodResult.response
            }
        }

        return lastSuccessResponse ?: findPaymentMethodResult.response
    }

    override suspend fun checkoutWithNewBankAccountCard(accountNumber: String): ResponseCallback<CheckoutResponse> {
        return updateCheckout(bankAccountId = accountNumber)
    }

    private suspend fun doCheckout(cardId: String? = null, bankAccountId: String? = null): ResponseCallback<CheckoutResponse> {
        val body = initRequestBodyMapper.map(paymentSettingRepository, cardId, bankAccountId)
        val preferenceId = paymentSettingRepository.checkoutPreferenceId
        val apiResponse = networkApi.apiCallForResponse(CheckoutService::class.java) {
            if (preferenceId != null) {
                it.checkout(preferenceId, body)
            } else {
                it.checkout(body)
            }
        }
        return when (apiResponse) {
            is ApiResponse.Failure -> Response.Failure(
                MercadoPagoError(
                    apiResponse.exception,
                    ApiUtil.RequestOrigin.POST_INIT
                )
            )
            is ApiResponse.Success -> Response.Success(apiResponse.result)
        }
    }

    private suspend fun checkoutAndFindPaymentMethod(cardId: String?, bankAccountId: String?): FindPaymentMethodResult {
        return when (val response = doCheckout(cardId, bankAccountId)) {
            is Response.Success -> {
                val retryNeeded = response.result.retry?.isNeeded ?: true
                FindPaymentMethodResult(response, retryNeeded)
            }
            is Response.Failure -> FindPaymentMethodResult(response)
        }
    }

    data class FindPaymentMethodResult(
        val response: ResponseCallback<CheckoutResponse>,
        val retryNeeded: Boolean = false
    )

    companion object {
        private const val MAX_REFRESH_RETRIES = 3
        private const val RETRY_DELAY = 5000L
    }
}
