package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey;
import com.mercadopago.android.px.model.AmountConfiguration;

public interface ConfigurationSolver {

    /**
     * Retrieves the dominant discount hash for a custom option.
     *
     * @param customOptionId The custom option ID.
     * @return The hash associated to the discount configuration.
     */
    @NonNull
    String getConfigurationHashSelectedFor(@NonNull final String customOptionId);

    /**
     * Retrieves the dominant payer cost model for a custom option.
     *
     * @param customOptionId The custom option ID.
     * @return The payer cost model associated to the custom option ID.
     */
    @Nullable
    AmountConfiguration getAmountConfigurationSelectedFor(@NonNull final String customOptionId);

    /**
     * Retrieves the dominant payer cost model for a custom option.
     *
     * @param key The key.
     * @return The payer cost model associated to the custom option ID and payment method type ID.
     */
    @Nullable
    AmountConfiguration getAmountConfigurationFor(@NonNull final PayerPaymentMethodKey key);

    /**
     * Retrieves the dominant discount hash for a custom option.
     *
     * @param key The key.
     * @return The hash associated to the discount configuration.
     */
    @NonNull
    String getConfigurationHashFor(@NonNull final PayerPaymentMethodKey key);
}
