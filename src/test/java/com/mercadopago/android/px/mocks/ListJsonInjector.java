package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;
import java.util.Iterator;

/* default */ final class ListJsonInjector {

    private ListJsonInjector() {
    }

    /* default */ static void injectAll(@NonNull final Iterator<? extends JsonInjectable> iterator,
        @NonNull final StringBuilder jsonContainer) {
        if (!iterator.hasNext()) {
            return;
        }
        while (iterator.hasNext()) {
            final JsonInjectable value = iterator.next();
            if (iterator.hasNext()) {
                value.injectNext(jsonContainer);
            } else {
                value.inject(jsonContainer);
            }
        }
    }
}