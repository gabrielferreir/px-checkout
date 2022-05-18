package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.internal.adapters.NetworkApi
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.mappers.InitRequestBodyMapper
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.InitRequestBody

internal class PrefetchInitService(
    private val checkout: MercadoPagoCheckout,
    private val networkApi: NetworkApi,
    private val initRequestBodyMapper: InitRequestBodyMapper
) {

    suspend fun get(): Response<CheckoutResponse, ApiException> {
        val body = initRequestBodyMapper.map(checkout)

        return when (val apiResponse = getApiResponse(body)) {
            is ApiResponse.Success -> Response.Success(apiResponse.result)
            is ApiResponse.Failure -> Response.Failure(apiResponse.exception)
        }
    }

    private suspend fun getApiResponse(body: InitRequestBody): ApiResponseCallback<CheckoutResponse> {
        val preferenceId = checkout.preferenceId
        return networkApi.apiCallForResponse(CheckoutService::class.java) {
            if (preferenceId != null) {
                it.checkout(preferenceId, body)
            } else {
                it.checkout(body)
            }
        }
    }
}
