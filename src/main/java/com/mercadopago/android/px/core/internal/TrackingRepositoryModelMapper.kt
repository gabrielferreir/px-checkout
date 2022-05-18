package com.mercadopago.android.px.core.internal

import com.mercadopago.android.px.configuration.TrackingConfiguration
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.internal.mappers.Mapper

internal object TrackingRepositoryModelMapper : Mapper<TrackingConfiguration, TrackingRepository.Model>() {

    override fun map(configuration: TrackingConfiguration): TrackingRepository.Model {
        return TrackingRepository.Model(configuration.sessionId, configuration.flowId, configuration.flowDetail)
    }
}