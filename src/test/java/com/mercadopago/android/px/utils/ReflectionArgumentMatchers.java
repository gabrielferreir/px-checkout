package com.mercadopago.android.px.utils;

import androidx.annotation.NonNull;
import org.mockito.ArgumentMatchers;

public final class ReflectionArgumentMatchers {

    private ReflectionArgumentMatchers() {
    }

    @NonNull
    public static <T> T reflectionEquals(final T value, final String... excludeFields) {
        ArgumentMatchers.refEq(value, excludeFields);
        return value;
    }
}