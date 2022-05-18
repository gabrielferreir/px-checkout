package com.mercadopago.android.px.internal.datasource;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.core.FileManager;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.TransactionInfo;
import java.io.File;

public class UserSelectionService implements UserSelectionRepository {

    private static final String PREF_PRIMARY_SELECTED_PM = "PREF_PRIMARY_SELECTED_PAYMENT_METHOD";
    private static final String PREF_SECONDARY_SELECTED_PM = "PREF_SECONDARY_SELECTED_PAYMENT_METHOD";
    private static final String PREF_SELECTED_PAYER_COST = "PREF_SELECTED_INSTALLMENT";
    private static final String PREF_SELECTED_ISSUER = "PREF_SELECTED_ISSUER";
    private static final String FILE_SELECTED_CARD = "px_selected_card";
    private static final String CUSTOM_OPTION_ID = "custom_option_id";

    @NonNull private final SharedPreferences sharedPreferences;
    @NonNull private final FileManager fileManager;
    @Nullable private Card card;
    @NonNull private final File selectedCardFile;

    public UserSelectionService(@NonNull final SharedPreferences sharedPreferences, @NonNull final FileManager fileManager) {
        this.sharedPreferences = sharedPreferences;
        this.fileManager = fileManager;
        selectedCardFile = fileManager.create(FILE_SELECTED_CARD);
    }

    @Override
    public void removePaymentMethodSelection() {
        sharedPreferences.edit().remove(PREF_PRIMARY_SELECTED_PM).apply();
        removeSecondaryPaymentMethodSelection();
        removePayerCostSelection();
        removeIssuerSelection();
    }

    private void removeSecondaryPaymentMethodSelection() {
        sharedPreferences.edit().remove(PREF_SECONDARY_SELECTED_PM).apply();
    }

    private void removeIssuerSelection() {
        sharedPreferences.edit().remove(PREF_SELECTED_ISSUER).apply();
    }

    private void removePayerCostSelection() {
        sharedPreferences.edit().remove(PREF_SELECTED_PAYER_COST).apply();
    }

    private void removeCardSelection() {
        card = null;
        fileManager.removeFile(selectedCardFile);
        removePaymentMethodSelection();
        removeIssuerSelection();
        removePayerCostSelection();
    }

    private void removeCustomOptionId() {
        sharedPreferences.edit().remove(CUSTOM_OPTION_ID).apply();
    }

    /**
     * it's important to select and then add the installments there is a side effect after changing the payment method
     * that deletes the old payer cost cache
     *
     * @param primary new payment method selected.
     * @param secondary payment method selected.
     */
    @Override
    public void select(@Nullable final PaymentMethod primary, @Nullable final PaymentMethod secondary) {
        if (primary == null) {
            removePaymentMethodSelection();
        } else {
            sharedPreferences.edit().putString(PREF_PRIMARY_SELECTED_PM, JsonUtil.toJson(primary)).apply();

            if (secondary != null) {
                sharedPreferences.edit().putString(PREF_SECONDARY_SELECTED_PM, JsonUtil.toJson(secondary)).apply();
            } else {
                removeSecondaryPaymentMethodSelection();
            }

            removePayerCostSelection();
        }
    }

    @Override
    public void select(@NonNull final PayerCost payerCost) {
        sharedPreferences.edit().putString(PREF_SELECTED_PAYER_COST, JsonUtil.toJson(payerCost)).apply();
    }

    @Override
    public void select(@NonNull final Issuer issuer) {
        sharedPreferences.edit().putString(PREF_SELECTED_ISSUER, JsonUtil.toJson(issuer)).apply();
    }

    @Override
    public void select(@NonNull final String customOptionId) {
        sharedPreferences.edit().putString(CUSTOM_OPTION_ID, JsonUtil.toJson(customOptionId)).apply();
    }

    @Override
    public void select(@Nullable final Card card, @Nullable final PaymentMethod secondaryPaymentMethod) {
        if (card == null) {
            removeCardSelection();
        } else {
            this.card = card;
            fileManager.writeToFile(selectedCardFile, card);
            select(card.getPaymentMethod(), secondaryPaymentMethod);
            select(card.getIssuer());
        }
    }

    @Override
    @Nullable
    public PaymentMethod getPaymentMethod() {
        return JsonUtil.fromJson(sharedPreferences.getString(PREF_PRIMARY_SELECTED_PM, TextUtil.EMPTY),
            PaymentMethod.class);
    }

    @Nullable
    @Override
    public PaymentMethod getSecondaryPaymentMethod() {
        return JsonUtil.fromJson(sharedPreferences.getString(PREF_SECONDARY_SELECTED_PM, TextUtil.EMPTY),
            PaymentMethod.class);
    }

    @Override
    @Nullable
    public PayerCost getPayerCost() {
        return JsonUtil.fromJson(
            sharedPreferences.getString(PREF_SELECTED_PAYER_COST, TextUtil.EMPTY), PayerCost.class);
    }

    @Nullable
    @Override
    public Issuer getIssuer() {
        return JsonUtil.fromJson(sharedPreferences.getString(PREF_SELECTED_ISSUER, TextUtil.EMPTY), Issuer.class);
    }

    @Override
    @Nullable
    public String getCustomOptionId() {
        return JsonUtil.fromJson(sharedPreferences.getString(CUSTOM_OPTION_ID, TextUtil.EMPTY),
            String.class);
    }

    @Nullable
    @Override
    public Card getCard() {
        if (card == null) {
            card = fileManager.readParcelable(selectedCardFile, Card.CREATOR);
        }
        return card;
    }

    @Override
    public void reset() {
        removePayerCostSelection();
        removePaymentMethodSelection();
        removeIssuerSelection();
        removeCardSelection();
        removeCustomOptionId();
    }
}