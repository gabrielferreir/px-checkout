package com.mercadopago.android.px.internal.util;

import android.content.Context;
import android.os.Vibrator;

public final class VibrationUtils {

    private static final int SMALL = 10;

    private VibrationUtils() {
    }

    public static void smallVibration(final Context context) {
        try {
            final Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                v.vibrate(SMALL);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
