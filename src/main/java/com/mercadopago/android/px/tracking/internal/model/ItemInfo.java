package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import com.mercadopago.android.px.model.Item;

@SuppressWarnings("unused")
@Keep
public class ItemInfo extends TrackingMapModel {

    private int quantity;
    private ItemDetail item;

    public ItemInfo(final Item val) {
        quantity = val.getQuantity();
        item = new ItemDetail(val);
    }
}
