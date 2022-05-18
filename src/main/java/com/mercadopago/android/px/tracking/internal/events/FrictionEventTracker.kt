package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.addons.model.Track
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.TrackFactory.withEvent
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.ApiErrorData
import java.util.*

open class FrictionEventTracker protected constructor(private val path: String, private val fId: Id,
    private val style: Style) : TrackWrapper() {

    protected val extraInfo: MutableMap<String, Any>

    enum class Id(val value: String) {
        GENERIC("px_generic_error"),
        SILENT("px_silent_error"),
        TOKEN_API_ERROR("token_api_error"),
        PAYMENTS_API_ERROR("payments_api_error"),
        NO_CONNECTION("no_connection"),
        INVALID_BIN("invalid_bin"),
        INVALID_CC_NUMBER("invalid_cc_number"),
        INVALID_NAME("invalid_name"),
        INVALID_EXP_DATE("invalid_expiration_date"),
        INVALID_CVV("invalid_cvv"),
        INVALID_DOCUMENT("invalid_document_number"),
        INVALID_STATUS_DETAIL("invalid_status_detail"),
        INVALID_ESC("invalid_esc"),
        INVALID_FINGERPRINT("invalid_fingerprint"),
        INVALID_POST_PAYMENT_DEEP_LINK("invalid_post_payment_deep_link"),
        INVALID_POST_PAYMENT_CREATE_RESULT("invalid_post_payment_create_result"),
        EXECUTE_USE_CASE("error_execute_use_case");

        companion object {
            const val ATTR = "id"
        }
    }

    enum class Style(val value: String) {
        SNACKBAR("snackbar"),
        SCREEN("screen"),
        CUSTOM_COMPONENT("custom_component"),
        NON_SCREEN("non_screen");

        companion object {
            const val ATTR = "style"
        }
    }

    override fun getTrack(): Track {
        val eventData: MutableMap<String, Any> = HashMap()
        eventData[Id.ATTR] = fId.value
        eventData[Style.ATTR] = style.value
        eventData[ATTR_PATH] = path
        eventData[ATTR_ATTRIBUTABLE] = VALUE_ATTRIBUTABLE
        eventData[ATTR_EXTRA_INFO] = extraInfo
        return withEvent(PATH).addData(eventData).build()
    }

    companion object {
        const val PATH = "/friction"
        private const val ATTR_PATH = "path"
        private const val ATTR_ATTRIBUTABLE = "attributable_to"
        private const val VALUE_ATTRIBUTABLE = "mercadopago"
        private const val ATTR_EXTRA_INFO = "extra_info"

        @JvmStatic
        fun with(fId: Id, track: TrackWrapper, style: Style, mercadoPagoError: MercadoPagoError): FrictionEventTracker {
            return with(track.getTrack()?.path.orEmpty(), fId, style, mercadoPagoError)
        }

        @JvmStatic
        fun with(path: String, fId: Id, style: Style, mercadoPagoError: MercadoPagoError): FrictionEventTracker {
            val frictionEventTracker = FrictionEventTracker(path, fId, style)
            frictionEventTracker.extraInfo["api_error"] = ApiErrorData(mercadoPagoError).toMap()
            return frictionEventTracker
        }

        @JvmStatic
        fun with(path: String, fId: Id, style: Style, stacktrace: String): FrictionEventTracker {
            val frictionEventTracker = FrictionEventTracker(path, fId, style)
            frictionEventTracker.extraInfo["stacktrace"] = stacktrace
            return frictionEventTracker
        }

        @JvmStatic
        fun with(path: String, fId: Id, style: Style, metadata: Map<String, Any>): FrictionEventTracker {
            val frictionEventTracker = FrictionEventTracker(path, fId, style)
            frictionEventTracker.extraInfo.putAll(metadata)
            return frictionEventTracker
        }

        @JvmStatic
        fun with(fId: Id, track: TrackWrapper, style: Style) = with(track.getTrack()?.path.orEmpty(), fId, style)

        @JvmStatic
        fun with(path: String, fId: Id, style: Style) = FrictionEventTracker(path, fId, style)
    }

    init {
        extraInfo = HashMap()
    }
}