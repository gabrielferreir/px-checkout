package com.mercadopago.android.px.internal.features.one_tap.animations;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import com.mercadopago.android.px.R;

import static com.mercadopago.android.px.internal.util.ViewUtils.cancelAnimation;
import static com.mercadopago.android.px.internal.util.ViewUtils.shouldGoneAnim;
import static com.mercadopago.android.px.internal.util.ViewUtils.shouldVisibleAnim;

public class FadeAnimator {

    private final int normalDuration;
    private final int fastDuration;
    private final int fastestDuration;

    public FadeAnimator(@NonNull final Context context) {
        normalDuration = context.getResources().getInteger(R.integer.px_long_animation_time);
        fastDuration = context.getResources().getInteger(R.integer.px_short_animation_time);
        fastestDuration = context.getResources().getInteger(R.integer.px_shortest_animation_time);
    }

    private AlphaAnimation createFade(final int from, final int to) {
        final AlphaAnimation anim = new AlphaAnimation(from, to);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setFillAfter(true);
        return anim;
    }

    public void fadeIn(@NonNull final View viewToAnimate) {
        if (shouldVisibleAnim(viewToAnimate)) {
            final AlphaAnimation fade = createFade(0, 1);
            fade.setDuration(normalDuration);
            viewToAnimate.startAnimation(fade);
        } else {
            cancelAnimation(viewToAnimate);
            viewToAnimate.clearAnimation();
        }
        viewToAnimate.setVisibility(View.VISIBLE);
    }

    public void fadeInFastest(@NonNull final View viewToAnimate) {
        if (shouldVisibleAnim(viewToAnimate)) {

            final AlphaAnimation fade = createFade(0, 1);
            fade.setDuration(fastestDuration);
            viewToAnimate.startAnimation(fade);
        } else {
            cancelAnimation(viewToAnimate);
            viewToAnimate.clearAnimation();
        }
        viewToAnimate.setVisibility(View.VISIBLE);
    }

    public void fadeOut(@NonNull final View viewToAnimate) {
        if (shouldGoneAnim(viewToAnimate)) {
            viewToAnimate.clearAnimation();
            viewToAnimate.setVisibility(View.VISIBLE);
            final AlphaAnimation fade = createFade(1, 0);
            fade.setAnimationListener(new FadeAnimationListener(viewToAnimate, View.INVISIBLE));
            fade.setDuration(normalDuration);
            viewToAnimate.startAnimation(fade);
        } else {
            cancelAnimation(viewToAnimate);
            viewToAnimate.clearAnimation();
            viewToAnimate.setVisibility(View.INVISIBLE);
        }
    }

    public void fadeOutFast(@NonNull final View viewToAnimate) {
        if (shouldGoneAnim(viewToAnimate)) {
            viewToAnimate.setVisibility(View.VISIBLE);
            final AlphaAnimation fade = createFade(1, 0);
            fade.setAnimationListener(new FadeAnimationListener(viewToAnimate, View.INVISIBLE));
            fade.setDuration(fastDuration);
            viewToAnimate.startAnimation(fade);
        } else {
            cancelAnimation(viewToAnimate);
            viewToAnimate.clearAnimation();
            viewToAnimate.setVisibility(View.INVISIBLE);
        }
    }
}
