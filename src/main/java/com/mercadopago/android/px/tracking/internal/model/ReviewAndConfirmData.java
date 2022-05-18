package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("unused")
@Keep
public class ReviewAndConfirmData extends AvailableMethod {

    @NonNull private final List<ItemInfo> items;
    @NonNull private final BigDecimal totalAmount;
    @Nullable private final DiscountInfo discount;

    public ReviewAndConfirmData(@NonNull final AvailableMethod method,
        @NonNull final List<ItemInfo> itemInfos,
        @NonNull final BigDecimal totalAmount,
        @Nullable final DiscountInfo discount) {
        super(method.paymentMethodId, method.paymentMethodType);
        items = itemInfos;
        this.totalAmount = totalAmount;
        this.discount = discount;
    }
}
