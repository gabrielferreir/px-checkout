package com.mercadopago.android.px.internal.view;

import android.os.SystemClock;
import android.view.View;

public abstract class OnSingleClickListener implements View.OnClickListener {

    private long lastClickTime;

    @Override
    public final void onClick(final View view) {
        // hack needed to avoid listener being called multiple times by fast multiple clicks due to the fact that
        // Android enqueue events right away and we can't disable the button in time
        if (SystemClock.elapsedRealtime() - lastClickTime < 2000) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        onSingleClick(view);
    }

    public abstract void onSingleClick(final View v);
}