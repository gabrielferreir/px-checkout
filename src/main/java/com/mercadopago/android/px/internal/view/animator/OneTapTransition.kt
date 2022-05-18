package com.mercadopago.android.px.internal.view.animator

import android.graphics.Color
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.view.SummaryView

private const val DURATION = 700L
private const val OFFSET = 350L
private const val SECURITY_CODE_ENTER_DELAY = 550L

class OneTapTransition(private val slider: View,
    private val summary: SummaryView,
    private val button: View,
    private val sliderHeader: View,
    private val scrollIndicator: View,
    private val split: View,
    private val oneTapContainer: View
) {

    fun playEnterFromCardForm() {
        val context = slider.context

        AnimationUtils.loadAnimation(context, R.anim.px_summary_slide_up_in).also {
            it.startOffset = OFFSET
            button.startAnimation(it)
        }

        AnimationUtils.loadAnimation(context, R.anim.px_summary_slide_up_in).also {
            slider.startAnimation(it)
        }

        AnimationUtils.loadAnimation(context, R.anim.px_fade_in).also {
            it.duration = DURATION
            it.startOffset = OFFSET
            sliderHeader.startAnimation(it)
            split.startAnimation(it)
            scrollIndicator.startAnimation(it)
        }

        summary.animateEnter(DURATION)
    }

    fun playEnterFromSecurityCode() {
        with(oneTapContainer) {
            setBackgroundColor(ContextCompat.getColor(context, R.color.px_checkout_background))
        }
        with(slider) {
            visibility = View.INVISIBLE
            postDelayed({ visibility = View.VISIBLE }, SECURITY_CODE_ENTER_DELAY)
        }

        with(button) {
            visibility = View.INVISIBLE
            postDelayed({ visibility = View.VISIBLE }, SECURITY_CODE_ENTER_DELAY)
        }

        AnimationUtils.loadAnimation(slider.context, R.anim.px_fade_in).also {
            it.duration = DURATION
            it.startOffset = OFFSET
            sliderHeader.startAnimation(it)
            split.startAnimation(it)
            scrollIndicator.startAnimation(it)
        }

        summary.animateEnter(DURATION)
    }

    fun playExitToCardForm() {
        val context = slider.context

        AnimationUtils.loadAnimation(context, R.anim.px_summary_slide_down_out).also {
            slider.startAnimation(it)
            button.startAnimation(it)
        }

        AnimationUtils.loadAnimation(context, R.anim.px_fade_out).also {
            it.duration = DURATION

            sliderHeader.startAnimation(it)
            scrollIndicator.startAnimation(it)
            if (split.visibility == View.VISIBLE) {
                split.startAnimation(it)
            }
        }

        summary.animateExit(OFFSET)
    }

    fun playExitToSecurityCode() {
        oneTapContainer.setBackgroundColor(Color.TRANSPARENT)
        slider.postDelayed({
            slider.clearAnimation()
            slider.visibility = View.INVISIBLE
        }, 100)

        AnimationUtils.loadAnimation(slider.context, R.anim.px_fade_out).also {
            it.duration = DURATION
            sliderHeader.startAnimation(it)
            scrollIndicator.startAnimation(it)
            button.startAnimation(it)
            if (split.visibility == View.VISIBLE) {
                split.startAnimation(it)
            }
        }

        summary.animateExit(OFFSET)
    }
}