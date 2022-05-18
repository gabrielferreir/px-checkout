package com.mercadopago.android.px.internal.actions;

import com.mercadopago.android.px.model.Action;

public class LinkAction extends Action {

    public final String url;

    public LinkAction(final String url) {
        this.url = url;
    }
}
