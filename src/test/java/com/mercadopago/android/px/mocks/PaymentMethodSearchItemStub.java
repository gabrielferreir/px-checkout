package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.utils.ResourcesUtil;

public enum PaymentMethodSearchItemStub implements JsonInjectable<PaymentMethodSearchItem> {
    CREDIT_CARD("group_credit_card.json"),
    TICKETS_MLA("group_tickets_MLA.json"),
    PAGOFACIL("group_pagofacil.json"),
    BOLBRADESCO("group_bolbradesco.json");

    public static PaymentMethodSearchItemStub[] ONLY_TICKETS_MLA = { TICKETS_MLA };
    public static PaymentMethodSearchItemStub[] ONLY_BOLBRADESCO_MLB = { BOLBRADESCO };
    public static PaymentMethodSearchItemStub[] ONLY_CREDIT_CARD = { CREDIT_CARD };
    public static PaymentMethodSearchItemStub[] CREDIT_CARD_AND_PAGO_FACIL = { CREDIT_CARD, PAGOFACIL };

    @NonNull final String fileName;

    PaymentMethodSearchItemStub(@NonNull final String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    @Override
    public PaymentMethodSearchItem get() {
        return JsonUtil.fromJson(getJson(), PaymentMethodSearchItem.class);
    }

    @NonNull
    @Override
    public String getJson() {
        return ResourcesUtil.getStringResource(fileName);
    }

    @NonNull
    @Override
    public String getType() {
        return "%PAYMENT_METHOD_SEARCH_ITEM%";
    }
}