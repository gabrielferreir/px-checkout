package com.mercadopago.android.px.internal.domain

import android.net.Uri
import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.ifSuccess
import com.mercadopago.android.px.internal.repository.CheckoutRepository
import com.mercadopago.android.px.internal.util.encryptToSha1
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker

private const val EXTERNAL_ACCOUNT_ID = "external_account_id"

internal class CheckoutWithNewBankAccountCardUseCase(
    private val checkoutRepository: CheckoutRepository,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<Uri, CheckoutResponse>(tracker) {

    override suspend fun doExecute(param: Uri): Response<CheckoutResponse, MercadoPagoError> {
        val externalAccountId = param.getQueryParameter(EXTERNAL_ACCOUNT_ID)
        checkNotNull(externalAccountId) { "External account id should not be null" }
        encryptToSha1(externalAccountId)?.let {
            return with(checkoutRepository) { checkoutWithNewBankAccountCard(it).ifSuccess(::configure) }
        } ?: run {
            val error =
                MercadoPagoError.createNotRecoverable("Account number encrypt not working")
            tracker.track(
                FrictionEventTracker.with(
                    "${TrackWrapper.BASE_PATH}/bank_account_added/",
                    FrictionEventTracker.Id.GENERIC,
                    FrictionEventTracker.Style.SCREEN,
                    error
                )
            )
            return Response.Failure(error)
        }
    }
}
