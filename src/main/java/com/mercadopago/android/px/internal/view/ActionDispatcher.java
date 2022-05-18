package com.mercadopago.android.px.internal.view;

import com.mercadopago.android.px.model.Action;

public interface ActionDispatcher {

    void dispatch(final Action action);
}