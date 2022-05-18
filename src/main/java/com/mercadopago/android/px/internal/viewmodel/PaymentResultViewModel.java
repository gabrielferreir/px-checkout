package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Action;

public final class PaymentResultViewModel {

    private final int titleResId;
    private final int bodyTitleResId;
    private final int titleDescriptionResId;
    private final boolean hasDetail;
    private final boolean showPaymentMethods;
    private final boolean isRecoverable;
    private final boolean isSuccess;
    private final boolean isPendingWarning;
    private final boolean isPendingSuccess;
    private final int descriptionResId;
    private final int secondDescriptionResId;
    private final int backgroundResId;
    private final int badgeResId;
    private final int iconResId;
    private final String descriptionParam;

    // Action metadata
    @Nullable private final Action mainAction;
    private final int mainActionTitle;
    @Nullable private final Action linkAction;
    private final int linkActionTitle;

    /* default */ PaymentResultViewModel(final Builder builder) {
        titleResId = builder.titleResId;
        hasDetail = builder.hasDetail;
        showPaymentMethods = builder.showPaymentMethods;
        mainAction = builder.mainAction;
        mainActionTitle = builder.mainActionTitle;
        linkAction = builder.linkAction;
        linkActionTitle = builder.linkActionTitle;
        isRecoverable = builder.isRecoverable;
        isSuccess = builder.isApprovedSuccess;
        isPendingWarning = builder.isPendingWarning;
        isPendingSuccess = builder.isPendingSuccess;
        descriptionResId = builder.descriptionResId;
        secondDescriptionResId = builder.secondDescriptionResId;
        bodyTitleResId = builder.bodyTitleResId;
        titleDescriptionResId = builder.titleDescriptionResId;
        backgroundResId = builder.backgroundResId;
        badgeResId = builder.badgeResId;
        iconResId = builder.iconResId;
        descriptionParam = builder.descriptionParam;
    }

    public boolean hasBodyTitle() {
        return bodyTitleResId != 0;
    }

    @StringRes
    public int getTitleResId() {
        return titleResId;
    }

    public boolean hasBodyError() {
        return hasDetail;
    }

    public boolean shouldShowPaymentMethods() {
        return showPaymentMethods;
    }

    public boolean isErrorRecoverable() {
        return isRecoverable;
    }

    public String getMainActionTitle(@NonNull final Context context) {
        return mainActionTitle == 0 ? TextUtil.EMPTY : context.getString(mainActionTitle);
    }

    @Nullable
    public Action getMainAction() {
        return mainAction;
    }

    @Nullable
    public Action getLinkAction() {
        return linkAction;
    }

    public String getLinkActionTitle(@NonNull final Context context) {
        return linkActionTitle == 0 ? TextUtil.EMPTY : context.getString(linkActionTitle);
    }

    public boolean isPendingWarning() {
        return isPendingWarning;
    }

    public boolean isPendingSuccess() {
        return isPendingSuccess;
    }

    public boolean isApprovedSuccess() {
        return isSuccess;
    }

    @NonNull
    public String getDescription(@NonNull final Context context) {
        return (descriptionResId == 0 ? TextUtil.EMPTY : getDescriptionText(context)) +
            (secondDescriptionResId == 0 ? TextUtil.EMPTY : TextUtil.NL + context.getString(secondDescriptionResId));
    }

    @NonNull
    private String getDescriptionText(@NonNull final Context context) {
        return descriptionParam != null ? TextUtil.format(context, descriptionResId, descriptionParam)
            : context.getString(descriptionResId);
    }

    public String getBodyTitle(@NonNull final Context context) {
        return bodyTitleResId == 0 ? TextUtil.EMPTY : context.getString(bodyTitleResId);
    }

    public int getBackgroundResId() {
        return backgroundResId;
    }

    public int getBadgeResId() {
        return badgeResId;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitleDescription(@NonNull final Context context) {
        return titleDescriptionResId == 0 ? TextUtil.EMPTY : context.getString(titleDescriptionResId);
    }

    public static class Builder {
        int titleResId;
        int descriptionResId;
        int secondDescriptionResId;
        int bodyTitleResId;
        int titleDescriptionResId;
        boolean isRecoverable;
        boolean hasDetail;
        boolean showPaymentMethods;
        boolean isApprovedSuccess;
        boolean isPendingWarning;
        boolean isPendingSuccess;
        int backgroundResId;
        int badgeResId;
        int iconResId;
        String descriptionParam;

        // Action metadata
        @Nullable Action mainAction;
        int mainActionTitle;

        @Nullable Action linkAction;
        int linkActionTitle;

        public Builder setTitleResId(final int titleResId) {
            this.titleResId = titleResId;
            return this;
        }

        public Builder setHasDetail(final boolean hasDetail) {
            this.hasDetail = hasDetail;
            return this;
        }

        public Builder setShowPaymentMethods(final boolean showPaymentMethods) {
            this.showPaymentMethods = showPaymentMethods;
            return this;
        }

        public Builder setMainAction(@Nullable final Action action) {
            mainAction = action;
            return this;
        }

        public Builder setMainActionTitle(final int titleResId) {
            mainActionTitle = titleResId;
            return this;
        }

        public Builder setLinkAction(@Nullable final Action action) {
            linkAction = action;
            return this;
        }

        public Builder setLinkActionTitle(final int actionTitleResId) {
            linkActionTitle = actionTitleResId;
            return this;
        }

        public Builder setIsErrorRecoverable(final boolean isRecoverable) {
            this.isRecoverable = isRecoverable;
            return this;
        }

        public Builder setApprovedSuccess(final boolean success) {
            isApprovedSuccess = success;
            return this;
        }

        public Builder setPendingWarning(final boolean pendingWarning) {
            isPendingWarning = pendingWarning;
            return this;
        }

        public Builder setPendingSuccess(final boolean pendingSuccess) {
            isPendingSuccess = pendingSuccess;
            return this;
        }

        public Builder setDescriptionResId(final int descriptionResId) {
            this.descriptionResId = descriptionResId;
            return this;
        }

        public Builder setSecondDescriptionResId(final int secondDescriptionResId) {
            this.secondDescriptionResId = secondDescriptionResId;
            return this;
        }

        public Builder setBodyTitleResId(final int bodyTitleResId) {
            this.bodyTitleResId = bodyTitleResId;
            return this;
        }

        public Builder setBodyDetailDescriptionResId(final int titleDescriptionResId) {
            this.titleDescriptionResId = titleDescriptionResId;
            return this;
        }

        public Builder setBackgroundColor(final int backgroundColor) {
            backgroundResId = backgroundColor;
            return this;
        }

        public Builder setIconResId(final int iconResId) {
            this.iconResId = iconResId;
            return this;
        }

        public Builder setBadgeResId(final int badgeResId) {
            this.badgeResId = badgeResId;
            return this;
        }

        public Builder setDescriptionResId(final int descriptionResId, final String param) {
            this.descriptionResId = descriptionResId;
            descriptionParam = param;
            return this;
        }

        public PaymentResultViewModel build() {
            return new PaymentResultViewModel(this);
        }
    }
}