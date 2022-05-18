package com.mercadopago.android.px.internal.util;

import android.content.Context;

public final class ScaleUtil {

    private ScaleUtil() {
    }

    public static int getPxFromDp(final int dpValue, final Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}