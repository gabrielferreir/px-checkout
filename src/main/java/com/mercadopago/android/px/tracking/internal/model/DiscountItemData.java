package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Nullable;

public final class DiscountItemData extends TrackingMapModel {

    @Nullable private final String campaignId;
    private final int index;

    public DiscountItemData(final int index, @Nullable final String campaignId) {
        this.index = index;
        this.campaignId = campaignId;
    }
}