package com.mercadopago.android.px.model;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@StringDef({ CheckoutType.ONE_TAP, CheckoutType.ONE_TAP_SELECTOR, CheckoutType.TRADITIONAL })

public @interface CheckoutType {
    String ONE_TAP = "one_tap";
    String ONE_TAP_SELECTOR = "one_tap_selector";
    @Deprecated
    String TRADITIONAL = "traditional";
}
