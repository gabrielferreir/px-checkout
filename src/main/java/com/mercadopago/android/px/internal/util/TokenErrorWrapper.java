package com.mercadopago.android.px.internal.util;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.model.EscDeleteReason;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.tracking.internal.model.Reason;
import java.util.List;

public final class TokenErrorWrapper {

    private final String tokenError;

    public TokenErrorWrapper(@NonNull final ApiException apiException) {
        tokenError = getTokenErrorFrom(apiException);
    }

    private String getTokenErrorFrom(@NonNull final ApiException apiException) {
        final StringBuilder concatTokenError = new StringBuilder();
        if (apiException.getStatus() == ApiUtil.StatusCodes.BAD_REQUEST) {
            final List<Cause> causes = apiException.getCause();
            if (causes != null && !causes.isEmpty()) {
                for (final Cause cause : causes) {
                    if (ApiException.ErrorCodes.INVALID_ESC.equals(cause.getCode()) ||
                        ApiException.ErrorCodes.INVALID_FINGERPRINT.equals(cause.getCode())) {
                        return cause.getCode();
                    } else {
                        concatTokenError.append(TextUtil.CSV_DELIMITER);
                        concatTokenError.append(cause.getCode());
                    }
                }
            }
        }
        return concatTokenError.toString().replaceFirst(TextUtil.CSV_DELIMITER, TextUtil.EMPTY);
    }

    public EscDeleteReason toEscDeleteReason() {
        switch (tokenError != null ? tokenError : TextUtil.EMPTY) {
        case ApiException.ErrorCodes.INVALID_ESC:
            return EscDeleteReason.INVALID_ESC;
        case ApiException.ErrorCodes.INVALID_FINGERPRINT:
            return EscDeleteReason.INVALID_FINGERPRINT;
        default:
            return EscDeleteReason.UNEXPECTED_TOKENIZATION_ERROR;
        }
    }

    public Reason toReason() {
        switch (tokenError != null ? tokenError : TextUtil.EMPTY) {
        case ApiException.ErrorCodes.INVALID_ESC:
            return Reason.INVALID_ESC;
        case ApiException.ErrorCodes.INVALID_FINGERPRINT:
            return Reason.INVALID_FINGERPRINT;
        default:
            return Reason.UNEXPECTED_TOKENIZATION_ERROR;
        }
    }

    public boolean isKnownTokenError() {
        return ApiException.ErrorCodes.INVALID_ESC.equals(tokenError) ||
            ApiException.ErrorCodes.INVALID_FINGERPRINT.equals(tokenError);
    }

    public String getValue() {
        return tokenError;
    }
}