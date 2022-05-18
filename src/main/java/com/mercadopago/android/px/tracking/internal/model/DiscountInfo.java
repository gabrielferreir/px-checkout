package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import java.math.BigDecimal;

@SuppressWarnings("unused")
@Keep
public final class DiscountInfo extends TrackingMapModel {

    private final BigDecimal couponAmount;
    private final BigDecimal amountOff;
    private final BigDecimal percentOff;
    private final BigDecimal maxCouponAmount;
    private final Integer maxRedeemPerUser;
    private final String campaignId;
    private final boolean isAvailable;

    private DiscountInfo(@Nullable final Discount discount, @Nullable final Campaign campaign,
        final boolean isAvailable) {

        this.isAvailable = isAvailable;
        if (campaign == null || discount == null) {
            couponAmount = null;
            amountOff = null;
            maxCouponAmount = null;
            maxRedeemPerUser = null;
            campaignId = null;
            percentOff = null;
        } else {
            couponAmount = discount.getCouponAmount();
            amountOff = discount.getAmountOff();
            percentOff = discount.getPercentOff();
            maxCouponAmount = campaign.getMaxCouponAmount();
            maxRedeemPerUser = campaign.getMaxRedeemPerUser();
            campaignId = discount.getId();
        }
    }

    @Nullable
    public static DiscountInfo with(@Nullable final Discount discount,
        @Nullable final Campaign campaign,
        final boolean isAvailable) {
        if (isAvailable && (discount == null || campaign == null)) {
            return null;
        }
        return new DiscountInfo(discount, campaign, isAvailable);
    }
}