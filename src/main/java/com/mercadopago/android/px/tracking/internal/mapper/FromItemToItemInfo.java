package com.mercadopago.android.px.tracking.internal.mapper;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.mappers.Mapper;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.tracking.internal.model.ItemInfo;

public class FromItemToItemInfo extends Mapper<Item, ItemInfo> {
    @Override
    public ItemInfo map(@NonNull final Item val) {
        return new ItemInfo(val);
    }
}
