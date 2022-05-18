package com.mercadopago.android.px.mocks

import com.mercadopago.android.px.internal.util.JsonUtil.fromJson
import com.mercadopago.android.px.model.Retry
import com.mercadopago.android.px.utils.ResourcesUtil

enum class RetryStub(private val fileName: String) : JsonInjectable<Retry?> {
    ONE_TAP_WITH_CARD_RETRY_NEEDED("one_tap_with_card_retry_needed.json"),
    ONE_TAP_NO_CARD_RETRY_NEEDED("one_tap_no_card_retry_needed.json");

    override fun get(): Retry {
        return fromJson(json, Retry::class.java)!!
    }

    override fun getJson(): String {
        return ResourcesUtil.getStringResource(fileName)
    }

    override fun getType(): String {
        return "%RETRY%"
    }
}
