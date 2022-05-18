package com.mercadopago.android.px.internal.experiments

import android.os.Parcel
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.util.KParcelable
import com.mercadopago.android.px.internal.util.parcelableCreator

abstract class Variant(var resVariant: Int) : KParcelable {
    abstract val default: Variant

    abstract fun process(handler: VariantHandler)
    fun isDefault() = resVariant == default.resVariant

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeInt(resVariant)
    }
}

class PulseVariant(resVariant: Int = R.layout.px_experiment_pulse) : Variant(resVariant) {
    override val default by lazy { PulseVariant(R.layout.px_experiment_arrow_default) }

    constructor(parcel: Parcel) : this(parcel.readInt())

    override fun process(handler: VariantHandler) {
        handler.visit(this)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::PulseVariant)
    }
}

class BadgeVariant(resVariant: Int = R.layout.px_experiment_badge) : Variant(resVariant) {
    override val default by lazy { BadgeVariant(R.layout.px_experiment_text_default) }

    constructor(parcel: Parcel) : this(parcel.readInt())

    override fun process(handler: VariantHandler) {
        handler.visit(this)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::BadgeVariant)
    }
}

class ScrolledVariant(resVariant: Int = R.layout.px_experiment_scrolled) : Variant(resVariant) {
    override val default by lazy { ScrolledVariant(R.layout.px_experiment_installments_default) }

    constructor(parcel: Parcel) : this(parcel.readInt())

    override fun process(handler: VariantHandler) {
        handler.visit(this)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::ScrolledVariant)
    }
}

enum class KnownExperiment(private val value: String) {
    INSTALLMENTS_HIGHLIGHT("px_nativo/highlight_installments");

    fun isExperiment(experimentName: String) = value == experimentName
}

enum class KnownVariant(private val experiment: KnownExperiment, val variant: Variant, private val value: String) {
    BADGE(KnownExperiment.INSTALLMENTS_HIGHLIGHT, BadgeVariant(), "badge"),
    PULSE(KnownExperiment.INSTALLMENTS_HIGHLIGHT, PulseVariant(), "animation_pulse"),
    SCROLLED(KnownExperiment.INSTALLMENTS_HIGHLIGHT, ScrolledVariant(), "scrolled_installments");

    fun isVariant(experimentName: String, variantName: String): Boolean {
        return this.experiment.isExperiment(experimentName) && value == variantName
    }
}