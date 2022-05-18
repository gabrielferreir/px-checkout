package com.mercadopago.android.px.internal.datasource

import android.util.MalformedJsonException
import com.mercadopago.android.px.addons.model.ThreeDSDataOnlyParams
import com.mercadopago.android.px.model.internal.CardHolderAuthenticatorBody
import com.mercadopago.android.px.internal.repository.CardHolderAuthenticatorRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.services.CardHolderAuthenticatorService
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.PaymentData

internal class CardHolderAuthenticatorRepositoryImpl(
    private val cardHolderAuthenticatorService: CardHolderAuthenticatorService,
    private val paymentSettingRepository: PaymentSettingRepository) : CardHolderAuthenticatorRepository {

    override suspend fun authenticate(paymentData: PaymentData, threeDSDataOnlyParams: ThreeDSDataOnlyParams): Any {
        val token = paymentData.token ?: throw IllegalStateException("Missing token during authentication")
        val sdkEphemPubKey = (JsonUtil.fromJson(threeDSDataOnlyParams.sdkEphemeralPublicKey, CardHolderAuthenticatorBody.SdkEphemPubKey::class.java)
            ?: throw MalformedJsonException("Malformed sdkEphemeralPublicKey"))
        val body = CardHolderAuthenticatorBody(
            paymentData.noDiscountAmount.toString(),
            CardHolderAuthenticatorBody.Card(
                token.cardHolder?.name.orEmpty(),
                paymentData.paymentMethod.id
            ),
            paymentSettingRepository.currency,
            paymentSettingRepository.site.id,
            threeDSDataOnlyParams.sdkAppId,
            threeDSDataOnlyParams.deviceData,
            sdkEphemPubKey,
            threeDSDataOnlyParams.sdkReferenceNumber,
            threeDSDataOnlyParams.sdkTransactionId
        )
        return cardHolderAuthenticatorService.authenticate(token.id, body)
    }
}
