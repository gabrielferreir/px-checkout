package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.CardMetadata;
import com.mercadopago.android.px.model.PayerCost;

@SuppressWarnings("unused")
@Keep
public final class CardExtraExpress extends CardExtraInfo {

    private final boolean hasSplit;

    private CardExtraExpress(@Nullable final String cardId, final boolean hasEsc, @Nullable final Long issuerId,
        @Nullable final PayerCostInfo payerCostTrackModel, final boolean hasSplit) {
        super(cardId, hasEsc, issuerId, payerCostTrackModel);
        this.hasSplit = hasSplit;
    }

    // esto es lo que se ofrece
    @NonNull
    public static CardExtraExpress expressSavedCard(final CardMetadata card, final boolean hasEsc,
        final boolean hasSplit) {
        return new CardExtraExpress(card.getId(),
            hasEsc,
            card.getDisplayInfo().getIssuerId(), null, hasSplit);
    }

    // este es el de confirm
    @NonNull
    public static CardExtraExpress selectedExpressSavedCard(final CardMetadata card, final PayerCost payerCost,
        final boolean hasEsc,
        final boolean hasSplit) {
        return new CardExtraExpress(card.getId(), hasEsc, card.getDisplayInfo().getIssuerId(), new PayerCostInfo(payerCost),
            hasSplit);
    }
}
