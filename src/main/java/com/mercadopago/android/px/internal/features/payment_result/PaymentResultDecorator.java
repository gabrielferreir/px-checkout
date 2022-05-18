package com.mercadopago.android.px.internal.features.payment_result;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultViewModel;

public final class PaymentResultDecorator {

    @NonNull
    public static PaymentResultDecorator from(@NonNull final PaymentResultViewModel viewModel) {
        final int image;
        // Whether payment result is approved or pending success, the colors remains the same
        int primaryColor = R.color.ui_components_success_color;

        if (viewModel.isApprovedSuccess()) {
            image = R.drawable.px_badge_check_icon;
        } else if (viewModel.isPendingSuccess()) {
            image = R.drawable.px_badge_pending_icon;
        } else if (viewModel.isPendingWarning() || viewModel.isErrorRecoverable()) {
            // With a pending or a recoverable error, we need warning colors
            primaryColor = R.color.ui_components_warning_color;

            //We need to differ the image whether is a pending warning or a recoverable error
            if (viewModel.isPendingWarning()) {
                image = R.drawable.px_badge_pending_icon;
            } else {
                image = R.drawable.px_badge_warning_icon;
            }
        } else {
            // Fallback with an error payment result
            image = R.drawable.px_badge_warning_icon;
            primaryColor = R.color.ui_components_error_color;
        }
        return new PaymentResultDecorator(primaryColor, image);
    }

    private final int primaryColor;
    private final int statusIconResId;

    private PaymentResultDecorator(final int primaryColor, final int statusIconResId) {
        this.primaryColor = primaryColor;
        this.statusIconResId = statusIconResId;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getStatusIcon() {
        return statusIconResId;
    }
}