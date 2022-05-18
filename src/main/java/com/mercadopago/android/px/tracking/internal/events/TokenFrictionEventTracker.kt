package com.mercadopago.android.px.tracking.internal.events

class TokenFrictionEventTracker private constructor(error: String) : FrictionEventTracker(
    TOKEN_FRICTION_PATH,
    Id.TOKEN_API_ERROR,
    Style.NON_SCREEN) {

    init {
        extraInfo["stacktrace"] = error
    }

    companion object {
        private const val TOKEN_FRICTION_PATH = "/px_checkout/create_token"

        @JvmStatic
        fun create(error: String) = TokenFrictionEventTracker(error)
    }
}