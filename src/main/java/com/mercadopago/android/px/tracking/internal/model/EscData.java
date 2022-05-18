package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.exceptions.ApiException;
import java.util.List;

@SuppressWarnings("unused")
public class EscData extends TrackingMapModel {

    private static final String URL = "create_token";

    private String cardId;
    private int escLength;
    private ApiErrorData apiError;
    private String url;
    private boolean retryAvailable;

    public static EscData with(@NonNull final String cardId, @NonNull final CharSequence esc,
        @NonNull final ApiException apiException) {
        final EscData escData = new EscData();
        escData.cardId = cardId;
        escData.escLength = esc.length();
        escData.apiError = ApiErrorData.with(apiException);
        escData.url = URL;
        escData.retryAvailable = apiException.isRecoverable();
        return escData;
    }

    public static final class ApiErrorData {
        private int status;
        private List<Cause> causes;

        public static ApiErrorData with(@NonNull final ApiException apiException) {
            final ApiErrorData apiErrorData = new ApiErrorData();
            apiErrorData.status = apiException.getStatus();
            apiErrorData.causes = apiException.getCause();
            return apiErrorData;
        }
    }
}