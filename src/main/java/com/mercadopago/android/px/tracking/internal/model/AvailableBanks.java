package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.Issuer;
import java.util.ArrayList;
import java.util.List;

@Keep
@SuppressWarnings("unused")
public class AvailableBanks extends TrackingMapModel {

    private final List<IssuerData> availableBanks;

    public AvailableBanks(@NonNull final Iterable<Issuer> issuers) {
        availableBanks = new ArrayList<>();
        for (final Issuer issuer : issuers) {
            availableBanks.add(new IssuerData(issuer));
        }
    }
}
