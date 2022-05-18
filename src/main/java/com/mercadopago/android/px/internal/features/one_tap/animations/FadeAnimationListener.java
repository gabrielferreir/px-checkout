package com.mercadopago.android.px.internal.features.one_tap.animations;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;

public class FadeAnimationListener implements Animation.AnimationListener {

    @NonNull private final View targetView;
    private final int visibilityFlag;

    public FadeAnimationListener(@NonNull final View targetView, final int visibilityFlag) {
        this.targetView = targetView;
        this.visibilityFlag = visibilityFlag;
    }

    @Override
    public void onAnimationStart(final Animation animation) {
        // Do nothing.
    }

    @Override
    public void onAnimationEnd(final Animation animation) {
        targetView.setVisibility(visibilityFlag);
        targetView.clearAnimation();
    }

    @Override
    public void onAnimationRepeat(final Animation animation) {
        // Do nothing.
    }
}