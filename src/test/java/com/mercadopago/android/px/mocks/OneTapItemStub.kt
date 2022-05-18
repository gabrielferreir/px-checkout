package com.mercadopago.android.px.mocks

import com.mercadopago.android.px.internal.util.JsonUtil.fromJson
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.utils.ResourcesUtil


enum class OneTapItemStub(private val fileName: String) : JsonInjectable<OneTapItem?> {
    ONE_TAP_VISA_CREDIT_CARD("one_tap_visa_credit_card.json"),
    ONE_TAP_CREDIT_CARD_WITH_RETRY("one_tap_visa_credit_card_with_retry.json"),
    ONE_TAP_MASTER_CREDIT_CARD("one_tap_master_credit_card.json"),
    ONE_TAP_ACCOUNT_MONEY("one_tap_account_money.json");

    override fun get(): OneTapItem {
        return fromJson(json, OneTapItem::class.java)!!
    }

    override fun getJson(): String {
        return ResourcesUtil.getStringResource(fileName)
    }

    override fun getType(): String {
        return "%EXPRESS_METADATA%"
    }
}