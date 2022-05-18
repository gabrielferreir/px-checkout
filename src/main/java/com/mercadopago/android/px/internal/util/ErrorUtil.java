package com.mercadopago.android.px.internal.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.ErrorActivity;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

import static com.mercadopago.android.px.core.MercadoPagoCheckout.EXTRA_ERROR;

public final class ErrorUtil {

    public static final int ERROR_REQUEST_CODE = 94;

    private ErrorUtil() {
    }

    public static void startErrorActivity(@NonNull final Activity activity, @Nullable final MercadoPagoError error) {
        activity.startActivityForResult(getIntent(activity, error), ERROR_REQUEST_CODE);
    }

    public static void startErrorActivity(@NonNull final Fragment fragment, @Nullable final MercadoPagoError error) {
        if (fragment.getContext() != null) {
            fragment.startActivityForResult(getIntent(fragment.getContext(), error), ERROR_REQUEST_CODE);
        }
    }

    @NonNull
    private static Intent getIntent(@NonNull final Context context, @Nullable final MercadoPagoError error) {
        final Intent intent = new Intent(context, ErrorActivity.class);
        intent.putExtra(EXTRA_ERROR, error);
        return intent;
    }

    public static void showApiExceptionError(@NonNull final Activity activity,
        final ApiException apiException,
        final String requestOrigin) {

        final MercadoPagoError mercadoPagoError;
        final String errorMessage;

        if (!Session.getInstance().getNetworkModule().getConnectionHelper().hasConnection()) {
            errorMessage = activity.getString(R.string.px_no_connection_message);
            mercadoPagoError = new MercadoPagoError(errorMessage, true);
        } else {
            mercadoPagoError = new MercadoPagoError(apiException, requestOrigin);
        }
        ErrorUtil.startErrorActivity(activity, mercadoPagoError);
    }

    public static boolean isErrorResult(@Nullable final Intent data) {
        return data != null && data.getSerializableExtra(EXTRA_ERROR) != null;
    }
}
