package com.mercadopago.android.px.internal.datasource;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository;
import com.mercadopago.android.px.internal.repository.PayerCostSelectionRepository;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PayerCost;
import java.util.Map;

public class PayerCostSelectionRepositoryImpl implements PayerCostSelectionRepository {

    private static final String PREF_SELECTED_PAYER_COSTS = "PREF_SELECTED_PAYER_COSTS";

    @NonNull private final SharedPreferences sharedPreferences;
    @NonNull private final ApplicationSelectionRepository applicationSelectionRepository;
    @Nullable private Map<PayerPaymentMethodKey, Integer> selectedPayerCosts;

    public PayerCostSelectionRepositoryImpl(@NonNull final SharedPreferences sharedPreferences,
        @NonNull final ApplicationSelectionRepository applicationSelectionRepository) {
        this.sharedPreferences = sharedPreferences;
        this.applicationSelectionRepository = applicationSelectionRepository;
    }

    @Override
    public int get(@NonNull final String paymentMethodId) {
        final String paymentTypeId = applicationSelectionRepository.get(paymentMethodId).getPaymentMethod().getType();
        final PayerPaymentMethodKey key = new PayerPaymentMethodKey(paymentMethodId, paymentTypeId);
        final Integer selectedPayerCost = getSelectedPayerCosts().get(key);
        return selectedPayerCost != null ? selectedPayerCost : PayerCost.NO_SELECTED;
    }

    @Override
    public void save(@NonNull final String paymentMethodId, final int selectedPayerCost) {
        final String paymentTypeId = applicationSelectionRepository.get(paymentMethodId).getPaymentMethod().getType();
        final PayerPaymentMethodKey key = new PayerPaymentMethodKey(paymentMethodId, paymentTypeId);
        final Map<PayerPaymentMethodKey, Integer> selectedPayerCosts = getSelectedPayerCosts();
        selectedPayerCosts.put(key, selectedPayerCost);
        sharedPreferences.edit().putString(PREF_SELECTED_PAYER_COSTS, JsonUtil.toJson(selectedPayerCosts))
            .apply();
    }

    @Override
    public void reset() {
        sharedPreferences.edit().remove(PREF_SELECTED_PAYER_COSTS).apply();
        selectedPayerCosts = null;
    }

    @NonNull
    private Map<PayerPaymentMethodKey, Integer> getSelectedPayerCosts() {
        if (selectedPayerCosts == null) {
            final String selectedPayerCostsJson = sharedPreferences.getString(PREF_SELECTED_PAYER_COSTS, null);
            selectedPayerCosts =
                JsonUtil.getCustomMapFromJson(selectedPayerCostsJson, PayerPaymentMethodKey.class, Integer.class);
        }
        return selectedPayerCosts;
    }
}