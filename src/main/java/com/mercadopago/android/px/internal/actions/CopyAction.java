package com.mercadopago.android.px.internal.actions;

import com.mercadopago.android.px.model.Action;

public class CopyAction extends Action {
    public final String content;

    public CopyAction(final String content) {
        this.content = content;
    }
}
