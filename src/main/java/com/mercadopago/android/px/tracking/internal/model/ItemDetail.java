package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.Item;
import java.math.BigDecimal;

@SuppressWarnings("unused")
@Keep
/* default */ class ItemDetail extends TrackingMapModel {

    private final String id;
    private final String description;
    private final BigDecimal price;

    /* default */ ItemDetail(@NonNull final Item val) {
        id = val.getId();
        description = val.getDescription();
        price = val.getUnitPrice();
    }
}
