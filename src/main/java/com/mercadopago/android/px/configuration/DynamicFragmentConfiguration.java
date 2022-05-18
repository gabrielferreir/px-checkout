package com.mercadopago.android.px.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.core.DynamicFragmentCreator;
import java.io.Serializable;
import java.util.HashMap;

// Used by single player to inform charges and other payment special information.
// Single player usecase depends on amount and payment method.
/**
 * @deprecated groups will no longer be available anymore
 */
@Deprecated
@SuppressWarnings("unused")
public final class DynamicFragmentConfiguration implements Serializable {

    private final HashMap<FragmentLocation, DynamicFragmentCreator> creators;

    public enum FragmentLocation {
        TOP_PAYMENT_METHOD_REVIEW_AND_CONFIRM,
        BOTTOM_PAYMENT_METHOD_REVIEW_AND_CONFIRM
    }

    /* default */ DynamicFragmentConfiguration(@NonNull final Builder builder) {
        creators = builder.creators;
    }

    @Nullable
    public DynamicFragmentCreator getCreatorFor(@NonNull final FragmentLocation fragmentLocation) {
        return creators.get(fragmentLocation);
    }

    public boolean hasCreatorFor(@NonNull final FragmentLocation fragmentLocation) {
        return creators.containsKey(fragmentLocation);
    }

    /**
     * @deprecated groups will no longer be available anymore
     */
    @Deprecated
    public static final class Builder {

        /* default */ HashMap<FragmentLocation, DynamicFragmentCreator> creators = new HashMap<>();

        /**
         * @param location where dynamic fragment will be placed.
         * @param dynamicFragmentCreator your creator.
         */
        public Builder addDynamicCreator(@NonNull final FragmentLocation location,
            @NonNull final DynamicFragmentCreator dynamicFragmentCreator) {
            creators.put(location, dynamicFragmentCreator);
            return this;
        }

        /**
         * @deprecated groups will no longer be available anymore
         */
        @Deprecated
        public DynamicFragmentConfiguration build() {
            return new DynamicFragmentConfiguration(this);
        }
    }
}
