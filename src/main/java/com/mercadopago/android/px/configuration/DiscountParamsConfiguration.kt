package com.mercadopago.android.px.configuration

import java.io.Serializable

class DiscountParamsConfiguration private constructor(builder: Builder) : Serializable {
    /**
     * Get additional data needed to apply a specific discount.
     *
     * @return set of labels needed to apply a specific discount.
     */
    val labels = builder.labels

    @get:Deprecated("use productId in Advanced Configuration")
    val productId = builder.productId

    /**
     * Additional params for obtaining a discount.
     *
     * @return map with params
     */
    val additionalParams: Map<String, String> = builder.additionalParams

    class Builder {
        var labels = setOf<String>()
            private set

        var productId: String? = null
            private set

        val additionalParams = mutableMapOf<String, String>()

        /**
         * Set additional data needed to apply a specific discount.
         *
         * @param labels are additional data needed to apply a specific discount.
         * @return builder to keep operating.
         */
        fun setLabels(labels: Set<String>) = apply { this.labels = labels }

        @Deprecated("use productId in Advanced Configuration")
        fun setProductId(productId: String) = apply { this.productId = productId }

        /**
         * Add an additional param for obtaining a discount.
         *
         * @param pair of key and value.
         * @return builder to keep operating.
         */
        fun addAdditionalParam(pair: Pair<String, String>) = apply { additionalParams[pair.first] = pair.second }

        /**
         * Add additional params for obtaining a discount
         *
         * @param params map with params
         * @return builder to keep operating.
         */
        fun addAdditionalParams(params: Map<String, String>) = apply { additionalParams.putAll(params) }

        fun build() = DiscountParamsConfiguration(this)
    }
}