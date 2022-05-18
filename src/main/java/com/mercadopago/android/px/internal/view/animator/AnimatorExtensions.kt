package com.mercadopago.android.px.internal.view.animator

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

internal fun <T : View> T.fadeIn(duration: Long? = null): Animator {
    return alpha(1f).apply {
        duration?.let { this.duration = it }
    }
}

internal fun <T : View> T.fadeOut(duration: Long? = null): Animator {
    return alpha(0f).apply {
        duration?.let { this.duration = it }
    }
}

internal fun <T : View> T.scaleAndTranslateY(scaleFactor: Float, to: Float, from: Float? = null, duration: Long? = null): AnimatorSet {
    val scaleX = ObjectAnimator.ofFloat(this, "scaleX", scaleFactor)
    val scaleY = ObjectAnimator.ofFloat(this, "scaleY", scaleFactor)
    val translateY = translateY(to, from)
    return AnimatorSet().apply {
        play(scaleX).with(scaleY).with(translateY)
        duration?.let { this.duration = it }
    }
}

internal fun <T : View> T.translateY(to: Float, from: Float? = null, duration: Long? = null): Animator {
    return ObjectAnimator.ofFloat(this, "translationY", to).apply {
        from?.let { setFloatValues(to, it) }
        duration?.let { this.duration = it }
    }
}

internal fun <T : View> T.fadeInAndTranslateY(to: Float, from: Float? = null, duration: Long? = null): AnimatorSet {
    val translateY = translateY(to, from)
    val fadeIn = fadeIn()
    return AnimatorSet().apply {
        play(translateY).with(fadeIn)
        duration?.let { this.duration = it }
    }
}

internal fun <T : View> T.fadeOutAndTranslateY(to: Float, from: Float? = null, duration: Long? = null): AnimatorSet {
    val translateY = translateY(to, from)
    val fadeOut = fadeOut()
    return AnimatorSet().apply {
        play(translateY).with(fadeOut)
        duration?.let { this.duration = it }
    }
}

private fun <T : View> T.alpha(value: Float) = ObjectAnimator.ofFloat(this, "alpha", value)