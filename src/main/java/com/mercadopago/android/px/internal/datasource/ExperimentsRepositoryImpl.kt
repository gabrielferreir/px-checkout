package com.mercadopago.android.px.internal.datasource

import android.content.SharedPreferences
import com.mercadopago.android.px.addons.model.internal.Configuration
import com.mercadopago.android.px.addons.model.internal.Experiment
import com.mercadopago.android.px.internal.experiments.KnownExperiment
import com.mercadopago.android.px.internal.repository.ExperimentsRepository
import com.mercadopago.android.px.internal.util.JsonUtil

private const val PREF_EXPERIMENTS = "PREF_EXPERIMENTS"

internal class ExperimentsRepositoryImpl(private val sharedPreferences: SharedPreferences) : ExperimentsRepository {
    private var internalExperiments: List<Experiment>? = null
    override val experiments: List<Experiment>
        get() {
            if (internalExperiments == null) {
                internalExperiments = JsonUtil.getListFromJson(
                    sharedPreferences.getString(PREF_EXPERIMENTS, null), Experiment::class.java)
            }
            return internalExperiments ?: emptyList()
        }

    override fun getExperiments(trackingMode: Configuration.TrackingMode): List<Experiment> {
        return experiments.filter { experiment -> trackingMode.match(experiment.variant.configuration?.trackingMode) }
    }

    override fun getExperiment(knownExperiment: KnownExperiment): Experiment? {
        return experiments.firstOrNull() { knownExperiment.isExperiment(it.name) }
    }

    override fun configure(experiments: List<Experiment>?) {
        internalExperiments = experiments
        sharedPreferences.edit().apply {
            putString(PREF_EXPERIMENTS, JsonUtil.toJson(experiments))
            apply()
        }
    }

    override fun reset() {
        sharedPreferences.edit().remove(PREF_EXPERIMENTS).apply()
        internalExperiments = null
    }
}
