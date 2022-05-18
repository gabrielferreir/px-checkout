package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.preferences.CheckoutPreference;

import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubBuilderOneItemAndPayer;

public enum CheckoutPreferenceStub implements JsonInjectable<CheckoutPreference> {
    DEFAULT;

    @NonNull
    @Override
    public CheckoutPreference get() {
        return stubBuilderOneItemAndPayer().build();
    }

    @NonNull
    @Override
    public String getJson() {
        return JsonUtil.toJson(get());
    }

    @NonNull
    @Override
    public String getType() {
        return "%CHECKOUT_PREFERENCE%";
    }
}