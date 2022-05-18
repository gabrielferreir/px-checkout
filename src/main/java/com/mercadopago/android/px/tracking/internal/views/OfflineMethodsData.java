package com.mercadopago.android.px.tracking.internal.views;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.OfflinePaymentMethod;
import com.mercadopago.android.px.model.OfflinePaymentType;
import com.mercadopago.android.px.tracking.internal.model.AvailableOfflineMethod;
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* default */ final class OfflineMethodsData extends TrackingMapModel {

    /* default */ final Collection<AvailableOfflineMethod> availableMethods = new ArrayList<>();

    public static OfflineMethodsData createFrom(@NonNull final List<OfflinePaymentType> offlinePaymentTypes) {
        final OfflineMethodsData instance = new OfflineMethodsData();

        for (final OfflinePaymentType offlinePaymentType : offlinePaymentTypes) {
            for (final OfflinePaymentMethod offlinePaymentMethod : offlinePaymentType.getPaymentMethods()) {
                instance.availableMethods
                    .add(new AvailableOfflineMethod(offlinePaymentType.getId(), offlinePaymentMethod.getId()));
            }
        }
        return instance;
    }
}