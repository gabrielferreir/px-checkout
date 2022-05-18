package com.mercadopago.android.px.internal.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.PaymentTypes;

public final class MercadoPagoUtil {

    private static final String PACKAGE_NAME_MP = "com.mercadopago.wallet";
    private static final String PLATFORM_MP = "MP";
    private static final String PLATFORM_ML = "ML";
    private static final String INTERNAL = "INTERNAL";
    private static final String MP_WEBVIEW_DEEPLINK = "mercadopago://webview/?url=";
    private static final String ML_WEBVIEW_DEEPLINK = "meli://webview/?url=";

    private MercadoPagoUtil() {
    }

    public static boolean isCard(final String paymentTypeId) {
        return (paymentTypeId != null) &&
            (paymentTypeId.equals(PaymentTypes.CREDIT_CARD) || paymentTypeId.equals(PaymentTypes.DEBIT_CARD) ||
                paymentTypeId.equals(PaymentTypes.PREPAID_CARD));
    }

    public static boolean isMPInstalled(final PackageManager packageManager) {
        try {
            return packageManager != null && packageManager.getApplicationInfo(PACKAGE_NAME_MP, 0).enabled;
        } catch (final PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isMP(@NonNull final Context context) {
        return getPlatform(context).equals(PLATFORM_MP);
    }

    public static String getPlatform(@NonNull final Context context) {
        final String packageName = context.getApplicationInfo().packageName;
        return packageName.contains("com.mercadolibre") ? PLATFORM_ML : PLATFORM_MP;
    }

    public static Intent getSafeIntent(@NonNull final Context context, @NonNull final Uri uri) {
        return getSafeIntent(context).setData(uri);
    }

    private static Intent getSafeIntent(@NonNull final Context context) {
        return new Intent(Intent.ACTION_VIEW).setPackage(context.getPackageName()).putExtra(INTERNAL, true);
    }

    @NonNull
    public static Intent getIntent(@NonNull final String link) {
        return new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link));
    }

    @NonNull
    public static Intent getNativeOrWebViewIntent(@NonNull final Context context, @NonNull final String link) {
        if (link.startsWith("http")) {
            final String webViewPath = isMP(context) ? MP_WEBVIEW_DEEPLINK : ML_WEBVIEW_DEEPLINK;
            return getIntent(webViewPath + link);
        }
        return getIntent(link);
    }
}
