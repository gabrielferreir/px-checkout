package com.mercadopago.android.px.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.core.DynamicDialogCreator;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// Used by single player to inform charges and other payment special information.
// Single player usecase depends on amount and payment method.
// Loyalty shows its own review and confirm with a dialog over ours.
@SuppressWarnings("unused")
public final class DynamicDialogConfiguration implements Serializable {

    private final Map<DialogLocation, DynamicDialogCreator> creators;

    public enum DialogLocation {
        /**
         * @deprecated groups will no longer be available anymore
         */
        @Deprecated
        ENTER_REVIEW_AND_CONFIRM,
        TAP_ONE_TAP_HEADER,
    }

    /* default */ DynamicDialogConfiguration(@NonNull final Builder builder) {
        creators = builder.creators;
    }

    @Nullable
    public DynamicDialogCreator getCreatorFor(@NonNull final DialogLocation dialogLocation) {
        return creators.get(dialogLocation);
    }

    public boolean hasCreatorFor(@NonNull final DialogLocation fragmentLocation) {
        return creators.containsKey(fragmentLocation);
    }

    public static final class Builder {

        /* default */ HashMap<DialogLocation, DynamicDialogCreator> creators = new HashMap<>();

        /**
         * @param location where dynamic dialog will be placed.
         * @param dynamicDialogCreator your creator.
         */
        public Builder addDynamicCreator(@NonNull final DialogLocation location,
            @NonNull final DynamicDialogCreator dynamicDialogCreator) {
            creators.put(location, dynamicDialogCreator);
            return this;
        }

        public DynamicDialogConfiguration build() {
            return new DynamicDialogConfiguration(this);
        }
    }
}
