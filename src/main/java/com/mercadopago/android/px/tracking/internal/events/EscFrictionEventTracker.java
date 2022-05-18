package com.mercadopago.android.px.tracking.internal.events;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.tracking.internal.TrackWrapper;
import com.mercadopago.android.px.tracking.internal.model.EscData;
import java.util.List;

public final class EscFrictionEventTracker extends FrictionEventTracker {
    private static final String PATH = "/px_checkout/create_esc_token";

    public static TrackWrapper create(@NonNull final String cardId, @NonNull final CharSequence esc,
        @NonNull final ApiException apiException) {
        return FrictionEventTracker.with(PATH, getFrictionIdFromApiException(apiException),
            FrictionEventTracker.Style.NON_SCREEN, EscData.with(cardId, esc, apiException).toMap());
    }

    private static FrictionEventTracker.Id getFrictionIdFromApiException(@NonNull final ApiException apiException) {
        if (apiException.getStatus() == ApiUtil.StatusCodes.BAD_REQUEST) {
            final List<Cause> causes = apiException.getCause();
            if (causes != null && !causes.isEmpty()) {
                for (final Cause cause : causes) {
                    switch (cause.getCode()) {
                    case ApiException.ErrorCodes.INVALID_ESC:
                        return FrictionEventTracker.Id.INVALID_ESC;
                    case ApiException.ErrorCodes.INVALID_FINGERPRINT:
                        return FrictionEventTracker.Id.INVALID_FINGERPRINT;
                    default:
                        break;
                    }
                }
            }
        }
        return FrictionEventTracker.Id.INVALID_ESC;
    }

    private EscFrictionEventTracker(@NonNull final String path,
        @NonNull final Id fId,
        @NonNull final Style style) {
        super(path, fId, style);
    }
}