package com.mercadopago.android.px.utils;

import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.services.Callback;

public class StubFailMpCall<T> implements MPCall<T> {

    private final ApiException apiException;

    public StubFailMpCall(final ApiException apiException) {
        this.apiException = apiException;
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        callback.failure(apiException);
    }
}