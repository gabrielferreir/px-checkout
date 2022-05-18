package com.mercadopago.android.px.internal.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;

public interface UserSelectionRepository {

    void select(@Nullable final PaymentMethod primary, @Nullable final PaymentMethod secondary);

    void select(@Nullable final Card card, @Nullable final PaymentMethod secondaryPaymentMethod);

    void select(@NonNull final PayerCost payerCost);

    void select(@NonNull final Issuer issuer);

    void select(@NonNull final String customOptionId);

    @Nullable
    PaymentMethod getPaymentMethod();

    @Nullable
    PaymentMethod getSecondaryPaymentMethod();

    void removePaymentMethodSelection();

    @Nullable
    PayerCost getPayerCost();

    @Nullable
    Issuer getIssuer();

    @Nullable
    String getCustomOptionId();

    @Nullable
    Card getCard();

    void reset();
}