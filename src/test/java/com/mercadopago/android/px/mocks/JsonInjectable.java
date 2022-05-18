package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;

interface JsonInjectable<T> extends JsonStub<T> {

    String SEPARATOR = ",";

    default void inject(@NonNull final StringBuilder jsonContainer) {
        final int index = jsonContainer.indexOf(getType());
        if(index < 0) {
            throw new IllegalStateException("Json template does not have tag " + getType());
        }
        jsonContainer.replace(index, index + getType().length(), getJson());
    }

    default void injectNext(@NonNull final StringBuilder jsonContainer) {
        final int index = jsonContainer.indexOf(getType());
        if(index < 0) {
            throw new IllegalStateException("Json template does not have tag " + getType());
        }
        jsonContainer.replace(index, index + getType().length(), getJson() + SEPARATOR + getType());
    }
}