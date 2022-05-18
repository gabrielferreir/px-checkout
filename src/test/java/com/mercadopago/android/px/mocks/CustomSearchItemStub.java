package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.utils.ResourcesUtil;

public enum CustomSearchItemStub implements JsonInjectable<CustomSearchItem> {
    ACCOUNT_MONEY("ppm_account_money.json"),
    VISA_CREDIT("ppm_visa_credit.json"),
    MASTER_CREDIT("ppm_master_credit.json"),
    AMEX_CREDIT("ppm_amex_credit.json"),
    CONSUMER_CREDIT("ppm_consumer_credit.json");

    public static final CustomSearchItemStub[] ONLY_ACCOUNT_MONEY = { ACCOUNT_MONEY };

    @NonNull private final String fileName;

    CustomSearchItemStub(@NonNull final String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    @Override
    public CustomSearchItem get() {
        return JsonUtil.fromJson(getJson(), CustomSearchItem.class);
    }

    @NonNull
    @Override
    public String getJson() {
        return ResourcesUtil.getStringResource(fileName);
    }

    @NonNull
    @Override
    public String getType() {
        return "%CUSTOM_SEARCH_ITEM%";
    }
}