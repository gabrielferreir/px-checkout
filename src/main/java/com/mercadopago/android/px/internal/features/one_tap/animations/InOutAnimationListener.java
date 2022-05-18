package com.mercadopago.android.px.internal.features.one_tap.animations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;

public class InOutAnimationListener implements Animation.AnimationListener {

    @Nullable private final OnAnimationEndListener listener;
    @NonNull private final View targetView;
    private final int visibilityFlag;

    public InOutAnimationListener(@NonNull final View targetView, final int visibilityFlag) {
        this(targetView, visibilityFlag, null);
    }

    /* default */ InOutAnimationListener(@NonNull final View targetView, final int visibilityFlag,
        @Nullable final OnAnimationEndListener listener) {
        this.targetView = targetView;
        this.visibilityFlag = visibilityFlag;
        this.listener = listener;
    }

    @Override
    public void onAnimationStart(final Animation animation) {
    }

    @Override
    public void onAnimationEnd(final Animation animation) {
        targetView.setVisibility(visibilityFlag);
        if (listener != null) {
            listener.nextAnimation();
        }
    }

    @Override
    public void onAnimationRepeat(final Animation animation) {
    }

    public interface OnAnimationEndListener {
        void nextAnimation();
    }
}