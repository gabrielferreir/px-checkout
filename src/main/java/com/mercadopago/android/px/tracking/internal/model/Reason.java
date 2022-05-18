package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.PaymentRecovery;

public enum Reason {
    ESC_DISABLED,
    ESC_CAP,
    SAVED_CARD,
    CALL_FOR_AUTH,
    DISABLED_CARD,
    INVALID_ESC,
    INVALID_FINGERPRINT,
    UNEXPECTED_TOKENIZATION_ERROR,
    NO_REASON,
    LEGACY;

    @NonNull
    public static Reason from(@NonNull final PaymentRecovery paymentRecovery) {
        if (paymentRecovery.isStatusDetailCallForAuthorize()) {
            return Reason.CALL_FOR_AUTH;
        } else if (paymentRecovery.isStatusDetailCardDisabled()) {
            return Reason.DISABLED_CARD;
        } else if (paymentRecovery.isStatusDetailInvalidESC()) {
            return Reason.ESC_CAP;
        } else {
            // this should not happen
            return Reason.NO_REASON;
        }
    }
}