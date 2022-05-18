package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse

typealias ResponseCallback<D> = Response<D, MercadoPagoError>

interface CheckoutRepository {
    suspend fun checkout(): ResponseCallback<CheckoutResponse>
    suspend fun checkoutWithNewCard(cardId: String): ResponseCallback<CheckoutResponse>
    suspend fun checkoutWithNewBankAccountCard(accountNumber: String): ResponseCallback<CheckoutResponse>
    fun configure(checkoutResponse: CheckoutResponse)
}