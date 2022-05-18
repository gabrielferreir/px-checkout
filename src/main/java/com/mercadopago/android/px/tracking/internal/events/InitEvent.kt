package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.InitData

internal class InitEvent(paymentSettingRepository: PaymentSettingRepository) : TrackWrapper() {

    private val initData = InitData.from(paymentSettingRepository)

    override fun getTrack() = TrackFactory.withEvent(PATH).addData(initData.toMap()).build()

    companion object {
        private const val PATH = "$BASE_PATH/init"
    }
}