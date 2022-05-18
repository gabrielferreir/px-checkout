package com.mercadopago.android.px.internal.features.one_tap.slider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

public interface PaymentMethod {
    interface View extends MvpView {
        void updateView();

        void updateState();

        void updateHighlightText(@Nullable final String text);

        void disable();

        void animateHighlightMessageIn();

        void animateHighlightMessageOut();
    }

    interface Action {
        void onFocusIn();

        void onFocusOut();

        void onApplicationChanged(@NonNull final String paymentTypeId);

        void onTrackActivityNotFoundFriction(@NonNull final MercadoPagoError error);
    }
}