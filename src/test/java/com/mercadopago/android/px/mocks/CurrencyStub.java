package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.utils.ResourcesUtil;

public enum CurrencyStub implements JsonInjectable<Currency> {
    MLA("currency_MLA.json"),
    MLB("currency_MLB.json");

    @NonNull private final String fileName;

    CurrencyStub(@NonNull final String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    public Currency get() {
        return JsonUtil.fromJson(getJson(), Currency.class);
    }

    @NonNull
    @Override
    public String getJson() {
        return ResourcesUtil.getStringResource(fileName);
    }

    @NonNull
    @Override
    public String getType() {
        return "%CURRENCY%";
    }
}