package com.mercadopago.android.px.internal.util;

import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.model.Discount;

public final class DiscountHelper {

    private DiscountHelper() {
    }

    @NonNull
    public static String getDiscountDescription(@NonNull final Context context, @NonNull final Discount discount) {
        return TextUtil.isNotEmpty(discount.getName()) ?
            discount.getName() : context.getString(R.string.px_review_summary_discount);
    }
}
