package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;

interface JsonStub<T> {
    @NonNull
    T get();

    @NonNull
    String getJson();

    @NonNull
    String getType();
}