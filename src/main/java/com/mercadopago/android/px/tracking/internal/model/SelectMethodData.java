package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("unused")
@Keep
public class SelectMethodData extends TrackingMapModel {

    @NonNull private final List<AvailableMethod> availableMethods;
    @NonNull private final List<ItemInfo> items;
    @NonNull private final BigDecimal totalAmount;
    private final int availableMethodsQuantity;
    private final int disabledMethodsQuantity;

    public SelectMethodData(@NonNull final List<AvailableMethod> availableMethods, @NonNull final List<ItemInfo> items,
        @NonNull final BigDecimal totalAmount, final int disabledMethodsQuantity) {
        this.availableMethods = availableMethods;
        this.items = items;
        this.disabledMethodsQuantity = disabledMethodsQuantity;
        this.totalAmount = totalAmount;
        availableMethodsQuantity = availableMethods.size() - disabledMethodsQuantity;
    }
}