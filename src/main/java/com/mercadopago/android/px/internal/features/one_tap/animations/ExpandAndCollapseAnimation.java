package com.mercadopago.android.px.internal.features.one_tap.animations;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.mercadopago.android.px.R;

import static android.view.View.VISIBLE;
import static com.mercadopago.android.px.internal.util.ViewUtils.cancelAnimation;
import static com.mercadopago.android.px.internal.util.ViewUtils.shouldGoneAnim;
import static com.mercadopago.android.px.internal.util.ViewUtils.shouldVisibleAnim;

public class ExpandAndCollapseAnimation {

    private final Animation expandAnimation;
    private final Animation collapseAnimation;
    private final View targetView;

    public ExpandAndCollapseAnimation(@NonNull final View view) {
        targetView = view;
        expandAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.px_anim_expand);
        collapseAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.px_anim_collapse);
        collapseAnimation.setAnimationListener(new FadeAnimationListener(view, View.GONE));
    }

    /**
     * Expand the view is not animating
     */
    public void expand() {
        if (shouldVisibleAnim(targetView)) {
            targetView.clearAnimation();
            targetView.setVisibility(VISIBLE);
            targetView.startAnimation(expandAnimation);
        } else {
            cancelAnimation(targetView);
            targetView.clearAnimation();
            targetView.setVisibility(VISIBLE);
        }
    }

    /**
     * Collapse the view is not animating
     */
    public void collapse() {
        if (shouldGoneAnim(targetView)) {
            targetView.clearAnimation();
            targetView.startAnimation(collapseAnimation);
        } else {
            cancelAnimation(targetView);
            targetView.clearAnimation();
            targetView.setVisibility(View.GONE);
        }
    }

    public boolean shouldCollapse() {
        return targetView.getVisibility() != View.GONE;
    }
}
