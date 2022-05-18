package com.mercadopago.android.px.tracking.internal.mapper;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.mappers.NonNullMapper;
import com.mercadopago.android.px.model.internal.CongratsResponse;

public class FromDiscountItemToItemId extends NonNullMapper<CongratsResponse.Discount.Item, String> {

    @Override
    public String map(@NonNull final CongratsResponse.Discount.Item discountItem) {
        return discountItem.getCampaignId();
    }
}