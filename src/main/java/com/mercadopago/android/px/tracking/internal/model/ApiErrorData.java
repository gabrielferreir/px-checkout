package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

@SuppressWarnings("unused")
@Keep
public class ApiErrorData extends TrackingMapModel {
    private final String message;
    private final int status;
    private final List<Cause> causes;
    private final String url;
    private final boolean retryAvailable;

    public ApiErrorData(@NonNull final MercadoPagoError mercadoPagoError) {
        if (mercadoPagoError.isApiException()) {
            final ApiException apiException = mercadoPagoError.getApiException();
            status = apiException.getStatus();
            causes = apiException.getCause();
            url = mercadoPagoError.getRequestOrigin();
        } else {
            status = 0;
            causes = null;
            url = null;
        }
        message = mercadoPagoError.getMessage();
        retryAvailable = mercadoPagoError.isRecoverable();
    }
}
