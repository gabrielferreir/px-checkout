package com.mercadopago.android.px.configuration

import java.util.*

/**
 * Allows you to customize certain information in your events.
 */
class TrackingConfiguration internal constructor(builder: Builder) {
    @JvmField val sessionId = builder.sessionId
    val flowId = builder.flowId
    val flowDetail = builder.flowDetail

    class Builder {
        // By default create a new session id.
        internal var sessionId = UUID.randomUUID().toString()
        internal var flowId: String? = null
        internal var flowDetail: Map<String, Any>? = null

        /**
         * Unique identifier for your checkout experience session.
         *
         * @param sessionId unique identifier for the session to be started.
         * @return builder to keep operating.
         */
        fun sessionId(sessionId: String) = apply { this.sessionId = sessionId }

        /**
         * Set the id of the payment flow, example: /instore
         *
         * @param flowId Id of the payment flow.
         * @return builder to keep operating.
         */
        fun flowId(flowId: String) = apply { this.flowId = flowId }

        /**
         * Set detail data to be included with all the tracks.
         *
         * @param flowDetail Detail data.
         * @return builder to keep operating.
         */
        fun flowDetail(flowDetail: Map<String, Any>) = apply { this.flowDetail = flowDetail }

        /**
         * return a new configuration instance.
         *
         * @return tracking configuration instance.
         */
        fun build() = TrackingConfiguration(this)
    }
}