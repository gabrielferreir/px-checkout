package com.mercadopago.android.px.internal.viewmodel;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel;
import com.mercadopago.android.px.model.BusinessPayment;

public enum PaymentResultType {
    APPROVED("APPROVED", R.color.ui_components_success_color, R.drawable.px_badge_check,
        R.drawable.px_badge_check_icon),
    REJECTED("REJECTED", R.color.ui_components_error_color, R.drawable.px_badge_error,
        R.drawable.px_badge_warning_icon),
    PENDING("PENDING", R.color.ui_components_warning_color, R.drawable.px_badge_pending_orange,
        R.drawable.px_badge_warning_icon);

    public final String name;
    public final int resColor;
    public final int badge;
    public final int icon;
    public final int message;

    PaymentResultType(@NonNull final String name, @ColorRes final int resColor, @DrawableRes final int badge,
        @DrawableRes final int icon) {
        this(name, resColor, badge, icon, 0);
    }

    PaymentResultType(@NonNull final String name, @ColorRes final int resColor, @DrawableRes final int badge,
        @DrawableRes final int icon, @StringRes final int message) {
        this.name = name;
        this.resColor = resColor;
        this.badge = badge;
        this.icon = icon;
        this.message = message;
    }

    public static PaymentResultType from(final BusinessPayment.Decorator decorator) {
        switch (decorator) {
        case PENDING:
            return PENDING;
        case APPROVED:
            return APPROVED;
        case REJECTED:
            return REJECTED;
        default:
            throw new IllegalStateException("Invalid decorator");
        }
    }

    public static PaymentResultType from(final PaymentCongratsModel.CongratsType congratsType) {
        switch (congratsType) {
        case PENDING:
            return PENDING;
        case APPROVED:
            return APPROVED;
        case REJECTED:
            return REJECTED;
        default:
            throw new IllegalStateException("Invalid congratsType");
        }
    }
}