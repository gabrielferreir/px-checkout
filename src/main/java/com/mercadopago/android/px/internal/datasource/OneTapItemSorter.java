package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey;
import com.mercadopago.android.px.model.internal.Application;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;
import com.mercadopago.android.px.model.internal.OneTapItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* default */ public final class OneTapItemSorter {
    @NonNull private final List<OneTapItem> oneTapItems;
    @NonNull private final Map<PayerPaymentMethodKey, DisabledPaymentMethod> disabledPaymentMethods;
    @Nullable private String prioritizedCardId;

    /* default */ OneTapItemSorter(@NonNull final List<OneTapItem> oneTapItems,
        @NonNull final Map<PayerPaymentMethodKey, DisabledPaymentMethod> disabledPaymentMethods) {
        this.oneTapItems = oneTapItems;
        this.disabledPaymentMethods = disabledPaymentMethods;
    }

    public OneTapItemSorter setPrioritizedCardId(@NonNull final String prioritizedCardId) {
        this.prioritizedCardId = prioritizedCardId;
        return this;
    }

    /* default */ void sort() {
        final Iterator<OneTapItem> expressMetadataIterator = oneTapItems.iterator();
        final Collection<OneTapItem> disabledExpressMetadataList = new ArrayList<>();
        OneTapItem prioritizedCard = null;
        while (expressMetadataIterator.hasNext()) {
            final OneTapItem oneTapItem = expressMetadataIterator.next();
            if (oneTapItem.isNewCard() || oneTapItem.isOfflineMethods()) {
                break;
            } else if (oneTapItem.isCard() && oneTapItem.getCard().getId().equals(prioritizedCardId)) {
                expressMetadataIterator.remove();
                prioritizedCard = oneTapItem;
            } else {
                boolean allApplicationsDisabled = true;
                for (final Application application : oneTapItem.getApplications()) {
                    if (!disabledPaymentMethods.containsKey(
                        new PayerPaymentMethodKey(CustomOptionIdSolver.getByApplication(oneTapItem, application),
                            application.getPaymentMethod().getType()))) {
                        allApplicationsDisabled = false;
                    }
                }
                if (allApplicationsDisabled) {
                    expressMetadataIterator.remove();
                    disabledExpressMetadataList.add(oneTapItem);
                }
            }
        }
        oneTapItems.addAll(disabledExpressMetadataList);
        if (prioritizedCard != null) {
            oneTapItems.add(0, prioritizedCard);
        }
    }
}