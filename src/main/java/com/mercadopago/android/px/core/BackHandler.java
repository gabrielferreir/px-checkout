package com.mercadopago.android.px.core;

public interface BackHandler {

    /**
     *
     * @return returns true if back was handled, therefore, caller should not handle back itself
     */
    boolean handleBack();
}