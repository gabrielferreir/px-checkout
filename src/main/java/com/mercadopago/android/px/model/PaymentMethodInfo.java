package com.mercadopago.android.px.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import java.io.Serializable;

public class PaymentMethodInfo implements Serializable {
    private final String id;
    private final String name;
    private final String description;
    @DrawableRes public final int icon;

    public PaymentMethodInfo(@NonNull final String id,
        @NonNull final String name,
        @DrawableRes final int icon,
        @NonNull final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    @Deprecated
    public PaymentMethodInfo(@NonNull final String id,
        @NonNull final String name,
        @DrawableRes final int icon) {
        this.id = id;
        this.name = name;
        description = null;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getIcon() {
        return icon;
    }
}