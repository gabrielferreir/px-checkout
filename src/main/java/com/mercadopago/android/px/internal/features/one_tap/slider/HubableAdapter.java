package com.mercadopago.android.px.internal.features.one_tap.slider;

import android.view.View;

public abstract class HubableAdapter<T, V extends View> extends ViewAdapter<T, V> {
    /* default */ HubableAdapter(final V view) {
        super(view);
    }

    public abstract T getNewModels(HubAdapter.Model model);
}