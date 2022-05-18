package com.mercadopago.android.px.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import java.io.Serializable;

public final class CustomStringConfiguration implements Serializable {

    @Nullable private final String customPayButtonText;
    @Nullable private final String customPayButtonProgressText;
    @Nullable private final String totalDescriptionText;

    /* default */ CustomStringConfiguration(@NonNull final Builder builder) {
        customPayButtonText = builder.customPayButtonText;
        customPayButtonProgressText = builder.customPayButtonProgressText;
        totalDescriptionText = builder.totalDescriptionText;
    }

    /**
     * Let us know what the main verb is
     *
     * @return custom main verb or the default one
     * @deprecated is not used anymore in favor of {@link #getCustomPaymentVaultTitle()} and {@link
     * #getTotalDescriptionText()}
     */
    @Deprecated
    @StringRes
    public int getMainVerbStringResourceId() {
        return 0;
    }

    /**
     * Check if a custom payment vault title is setted
     *
     * @return true/false if customPaymentVaultTitle is (or not) empty
     */
    @Deprecated
    public boolean hasCustomPaymentVaultTitle() {
        return false;
    }

    /**
     * Obtain customized payment vault title
     *
     * @return Custom Payment Vault Title
     * @deprecated groups will no longer be available anymore
     */
    @Deprecated
    @Nullable
    public String getCustomPaymentVaultTitle() {
        return null;
    }

    /**
     * Obtain customized pay button text
     *
     * @return Custom Pay Button Text
     */
    @Nullable
    public String getCustomPayButtonText() {
        return customPayButtonText;
    }

    /**
     * Obtain customized pay button progress text
     *
     * @return Custom Pay Button Progress Text
     */
    @Nullable
    public String getCustomPayButtonProgressText() {
        return customPayButtonProgressText;
    }

    /**
     * Obtain customized pay button progress text
     *
     * @return Custom Pay Button Progress Text
     */
    @Nullable
    public String getTotalDescriptionText() {
        return totalDescriptionText;
    }

    public static class Builder {
        /* default */ String customPayButtonText;
        /* default */ String customPayButtonProgressText;
        /* default */ String totalDescriptionText;

        public Builder() {
        }

        /**
         * Used to replace the main verb in Payment Methods screen.
         *
         * @param mainVerbStringResId the string resource that will be used
         * @return builder to keep operating
         * @deprecated You have to use setCustomPaymentVaultTitle or setTotalDescriptionText method depends on the
         * screen
         */
        @SuppressWarnings("unused")
        @Deprecated
        public Builder setMainVerbStringResourceId(@StringRes final int mainVerbStringResId) {
            return this;
        }

        /**
         * Add the possibility to add a custom Title in payment vault screen.
         *
         * @param title Custom title to be setted
         * @return builder to keep operating
         * @deprecated groups will no longer be available anymore
         */
        @Deprecated
        @SuppressWarnings("unused")
        public Builder setCustomPaymentVaultTitle(@NonNull final String title) {
            return this;
        }

        /**
         * Set custom text to the pay button.
         *
         * @param text Custom text
         * @return builder to keep operating
         */
        @SuppressWarnings("unused")
        public Builder setCustomPayButtonText(@NonNull final String text) {
            customPayButtonText = text;
            return this;
        }

        /**
         * Set custom text to the pay button progress.
         *
         * @param text Custom text
         * @return builder to keep operating
         */
        @SuppressWarnings("unused")
        public Builder setCustomPayButtonProgressText(@NonNull final String text) {
            customPayButtonProgressText = text;
            return this;
        }

        /**
         * Set custom text to total description.
         *
         * @param text Custom text
         * @return builder to keep operating
         */
        @SuppressWarnings("unused")
        public Builder setTotalDescriptionText(@NonNull final String text) {
            totalDescriptionText = text;
            return this;
        }

        public CustomStringConfiguration build() {
            return new CustomStringConfiguration(this);
        }
    }
}