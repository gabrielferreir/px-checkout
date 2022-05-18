package com.mercadopago.android.px.internal.features.one_tap.slider;

public interface Focusable {

    void onFocusIn();

    void onFocusOut();

    boolean hasFocus();
}