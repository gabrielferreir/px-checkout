package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import com.mercadopago.android.px.model.Issuer;

@SuppressWarnings("unused")
@Keep
/* default*/ class IssuerData extends TrackingMapModel {

    private final Long id;
    private final String name;

    /* default*/ IssuerData(final Issuer issuer) {
        id = issuer.getId();
        name = issuer.getName();
    }
}
