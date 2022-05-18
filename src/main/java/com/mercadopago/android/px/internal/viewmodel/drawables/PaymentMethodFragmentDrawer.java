package com.mercadopago.android.px.internal.viewmodel.drawables;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public interface PaymentMethodFragmentDrawer {

    Fragment draw(@NonNull final DrawableFragmentItem drawableFragmentItem);

    Fragment draw(@NonNull final OtherPaymentMethodFragmentItem drawableItem);

    Fragment draw(@NonNull final ConsumerCreditsDrawableFragmentItem drawableItem);
}
