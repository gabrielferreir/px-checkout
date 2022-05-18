package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.internal.OneTapItem;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.BankInfoHelper;
import com.mercadopago.android.px.tracking.internal.mapper.FromApplicationToApplicationInfo;
import com.mercadopago.android.px.tracking.internal.mapper.FromExpressMetadataToAvailableMethods;
import com.mercadopago.android.px.tracking.internal.mapper.FromItemToItemInfo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@Keep
public final class OneTapData extends SelectMethodData {

    @Nullable private final DiscountInfo discount;

    private OneTapData(@NonNull final List<AvailableMethod> availableMethods, @NonNull final BigDecimal totalAmount,
        @Nullable final DiscountInfo discount, @NonNull final List<ItemInfo> items, final int disabledMethodsQuantity) {
        super(availableMethods, items, totalAmount, disabledMethodsQuantity);
        this.discount = discount;
    }

    @NonNull
    public static OneTapData createFrom(
        @NonNull final FromApplicationToApplicationInfo fromApplicationToApplicationInfo,
        final Iterable<OneTapItem> oneTapItems,
        final CheckoutPreference checkoutPreference, final DiscountConfigurationModel discountModel,
        @NonNull final Set<String> cardsWithEsc, @NonNull final Set<String> cardsWithSplit,
        final int disabledMethodsQuantity, @NonNull final BankInfoHelper bankInfoHelper) {

        final List<ItemInfo> itemInfoList = new FromItemToItemInfo().map(checkoutPreference.getItems());

        final DiscountInfo discountInfo =
            DiscountInfo.with(discountModel.getDiscount(), discountModel.getCampaign(), discountModel.isAvailable());

        return new OneTapData(
            new FromExpressMetadataToAvailableMethods(fromApplicationToApplicationInfo, cardsWithEsc, cardsWithSplit, bankInfoHelper)
                .map(oneTapItems),
            checkoutPreference.getTotalAmount(), discountInfo, itemInfoList, disabledMethodsQuantity);
    }
}