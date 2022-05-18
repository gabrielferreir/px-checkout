package com.mercadopago.android.px.internal.audio

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.model.PaymentResult
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker

internal class SelectPaymentSoundUseCase(
    tracker: MPTracker,
    private val paymentSettingRepository: PaymentSettingRepository,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<PaymentResult, AudioPlayer.Sound>(tracker) {

    override suspend fun doExecute(param: PaymentResult): Response<AudioPlayer.Sound, MercadoPagoError> {
        var sound = AudioPlayer.Sound.NONE
        if (paymentSettingRepository.configuration.sonicBrandingEnabled()) {
            sound = when {
                param.isApproved -> AudioPlayer.Sound.SUCCESS
                param.isRejected -> AudioPlayer.Sound.FAILURE
                else -> AudioPlayer.Sound.NONE
            }
        }

        return Response.Success(sound)
    }
}