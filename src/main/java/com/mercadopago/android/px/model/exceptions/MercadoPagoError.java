package com.mercadopago.android.px.model.exceptions;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import java.io.Serializable;

public class MercadoPagoError implements Serializable {
    private static final long serialVersionUID = 8365047557796395851L;
    private String message;
    private String errorDetail;
    private String requestOrigin;
    private ApiException apiException;
    private final boolean recoverable;

    public MercadoPagoError(@NonNull final String message, final boolean recoverable) {
        this.message = message;
        this.recoverable = recoverable;
    }

    public MercadoPagoError(@NonNull final String message, @NonNull final String detail, final boolean recoverable) {
        this.message = message;
        errorDetail = detail;
        this.recoverable = recoverable;
    }

    public MercadoPagoError(@NonNull final ApiException apiException, final String requestOrigin) {
        this(apiException, requestOrigin, apiException.isRecoverable());
    }

    private MercadoPagoError(@NonNull final ApiException apiException, final String requestOrigin,
        final boolean recoverable) {
        this.apiException = apiException;
        this.requestOrigin = requestOrigin;
        this.recoverable = recoverable;
    }

    @NonNull
    public static MercadoPagoError createRecoverable(@NonNull final String message) {
        return new MercadoPagoError(message, true);
    }

    @NonNull
    public static MercadoPagoError createNotRecoverable(@NonNull final String message) {
        return new MercadoPagoError(message, false);
    }

    @NonNull
    public static MercadoPagoError createNotRecoverable(@NonNull final String message, @NonNull final String detail) {
        return new MercadoPagoError(message, detail, false);
    }

    @NonNull
    public static MercadoPagoError createNotRecoverable(@NonNull final ApiException apiException,
        @NonNull final String requestOrigin) {
        return new MercadoPagoError(apiException, requestOrigin, false);
    }

    public ApiException getApiException() {
        return apiException;
    }

    public boolean isRecoverable() {
        return recoverable;
    }

    public String getMessage() {
        if (message == null && getApiException() != null && !TextUtil.isEmpty(getApiException().getMessage())) {
            return getApiException().getMessage();
        }
        return message;
    }

    public String getRequestOrigin() {
        return requestOrigin;
    }

    public String getErrorDetail() {
        return errorDetail == null ? "" : errorDetail;
    }

    public boolean isApiException() {
        return apiException != null;
    }

    public boolean isPaymentProcessing() {
        return getApiException() != null
            && getApiException().getStatus() == ApiUtil.StatusCodes.PROCESSING;
    }

    public boolean isInternalServerError() {
        return getApiException() != null
            && String.valueOf(getApiException().getStatus())
            .startsWith(ApiUtil.StatusCodes.INTERNAL_SERVER_ERROR_FIRST_DIGIT);
    }

    public boolean isBadRequestError() {
        return getApiException() != null
            && (getApiException().getStatus() == ApiUtil.StatusCodes.BAD_REQUEST);
    }

    public boolean isNoConnectivityError() {
        return getApiException() != null
            && (getApiException().getStatus() == ApiUtil.StatusCodes.NO_CONNECTIVITY_ERROR);
    }

    @Override
    public String toString() {
        return "MercadoPagoError{" +
            "message='" + message + '\'' +
            ", errorDetail='" + errorDetail + '\'' +
            ", requestOrigin='" + requestOrigin + '\'' +
            ", apiException=" + apiException +
            ", recoverable=" + recoverable +
            '}';
    }
}
