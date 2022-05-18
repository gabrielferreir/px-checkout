package com.mercadopago.android.px.internal.view.experiments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mercadopago.android.px.addons.model.internal.Experiment
import com.mercadopago.android.px.internal.experiments.KnownVariant
import com.mercadopago.android.px.internal.experiments.Variant

object ExperimentHelper {

    fun getVariantsFrom(experiments: List<Experiment>?, vararg knownVariants: KnownVariant) = knownVariants.map {
        getVariantFrom(experiments, it)
    }

    fun getVariantFrom(experiments: List<Experiment>?, knownVariant: KnownVariant): Variant {

        if (experiments != null) {
            for (experiment in experiments) {
                val variantName = experiment.variant.name
                if (knownVariant.isVariant(experiment.name, variantName)) {
                    return knownVariant.variant
                }
            }
        }
        return knownVariant.variant.default
    }

    @JvmOverloads
    fun applyExperimentViewBy(root: ViewGroup, variant: Variant, inflater: LayoutInflater =
        LayoutInflater.from(root.context)): View = inflater.inflate(variant.resVariant, root)
}
