package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

@SuppressWarnings("unused")
@Keep
public class AvailableOfflineMethod extends TrackingMapModel {

    @NonNull
    /* default */ final String paymentMethodId;
    @NonNull
    /* default */ final String paymentMethodType;

    public AvailableOfflineMethod(@NonNull final String paymentMethodType, @NonNull final String paymentMethodId) {
        this.paymentMethodType = paymentMethodType;
        this.paymentMethodId = paymentMethodId;
    }
}
