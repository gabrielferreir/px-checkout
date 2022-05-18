package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.addons.model.internal.Configuration
import com.mercadopago.android.px.addons.model.internal.Experiment
import com.mercadopago.android.px.internal.experiments.KnownExperiment

internal interface ExperimentsRepository {
    val experiments: List<Experiment>
    fun getExperiments(trackingMode: Configuration.TrackingMode): List<Experiment>
    fun getExperiment(knownExperiment: KnownExperiment): Experiment?
    fun configure(experiments: List<Experiment>?)
    fun reset()
}