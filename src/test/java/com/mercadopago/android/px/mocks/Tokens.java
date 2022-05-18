package com.mercadopago.android.px.mocks;

import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.utils.ResourcesUtil;

public final class Tokens {
    private Tokens() {
        //TODO it could be replaced for TokenStubs?
    }

    public static Token getVisaToken() {
        final String json = ResourcesUtil.getStringResource("token_visa.json");
        return JsonUtil.fromJson(json, Token.class);
    }

    public static Token getToken() {
        final String json = ResourcesUtil.getStringResource("token.json");
        return JsonUtil.fromJson(json, Token.class);
    }
}