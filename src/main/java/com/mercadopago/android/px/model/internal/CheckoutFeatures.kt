package com.mercadopago.android.px.model.internal

import com.google.gson.annotations.SerializedName

/**
 * Checkout features contains feature specific params and metadata about integration.
 */
internal class CheckoutFeatures(builder: Builder) {

    @SerializedName("one_tap")
    val express: Boolean
    val split: Boolean

    @SerializedName("odr")
    val odrFlag: Boolean
    val comboCard: Boolean
    val hybridCard: Boolean
    val pix: Boolean
    val customTaxesCharges: Boolean
    val styleVersion: String
    val threedsSdkVersion: String
    val taxableCharges: Boolean
    val cardsCustomTaxesCharges: Boolean

    @SerializedName("validations_programs")
    val validationPrograms: List<String>
    val debin: String
    val newPaymentMethodVersion: String

    /* default */
    init {
        express = builder.express
        split = builder.split
        odrFlag = builder.odrFlag
        comboCard = builder.comboCard
        hybridCard = builder.hybridCard
        pix = builder.pix
        customTaxesCharges = builder.customTaxesCharges
        validationPrograms = builder.validationPrograms
        styleVersion = STYLE_VERSION
        threedsSdkVersion = THREEDS_SDK_VERSION
        taxableCharges = builder.taxableCharges
        cardsCustomTaxesCharges = builder.cardsCustomTaxesCharges
        debin = DEBIN_VERSION
        newPaymentMethodVersion = NEW_PAYMENT_METHOD_VERSION
    }

    class Builder {
        var split = false
        var express = false
        var odrFlag = false
        var comboCard = false
        var hybridCard = false
        var pix = false
        var customTaxesCharges = false
        var taxableCharges = false
        var cardsCustomTaxesCharges = false
        var validationPrograms: MutableList<String> = mutableListOf()

        fun setSplit(split: Boolean) = apply { this.split = split }

        fun setExpress(express: Boolean) = apply { this.express = express }

        fun setOdrFlag(odrFlag: Boolean) = apply { this.odrFlag = odrFlag }

        fun setComboCard(comboCard: Boolean) = apply { this.comboCard = comboCard }

        fun setHybridCard(hybridCard: Boolean) = apply { this.hybridCard = hybridCard }

        fun setPix(pix: Boolean) = apply { this.pix = pix }

        fun setCustomTaxesCharges(customTaxesCharges: Boolean) = apply { this.customTaxesCharges = customTaxesCharges }

        fun setCardsCustomTaxesCharges(cardsCustomTaxesCharges: Boolean) =
            apply { this.cardsCustomTaxesCharges = cardsCustomTaxesCharges }

        fun setTaxableCharges(taxableCharges: Boolean) = apply { this.taxableCharges = taxableCharges }

        fun addValidationPrograms(validationPrograms: List<String>) =
            apply { this.validationPrograms.addAll(validationPrograms) }

        fun addValidationProgram(validationProgram: String) = apply { validationPrograms.add(validationProgram) }

        fun build() = CheckoutFeatures(this)
    }

    companion object {
        const val STYLE_VERSION = "v1"
        const val THREEDS_SDK_VERSION = "v1"
        const val DEBIN_VERSION = "v1"
        const val NEW_PAYMENT_METHOD_VERSION = "v1"
    }
}
