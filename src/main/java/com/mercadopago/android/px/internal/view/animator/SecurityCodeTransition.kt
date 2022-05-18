package com.mercadopago.android.px.internal.view.animator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.meli.android.carddrawer.model.CardDrawerView
import com.mercadopago.android.px.internal.extensions.runWhenLaidOut

private const val EXTRA_ANIM_DISTANCE = "bundle_anim_distance"
private const val DEFAULT_DURATION = 600L

class SecurityCodeTransition(private val parentView: ConstraintLayout,
    private val card: CardDrawerView,
    private val toolbar: View,
    private val title: View,
    private val subtitle: View,
    private val textField: View,
    private val button: View) {

    var listener: Listener? = null
    private var cardAnimationDistance: Float = 0f

    fun playEnter() {
        textField.runWhenLaidOut {
            card.pivotX = card.measuredWidth * 0.5f
            card.pivotY = 0f
            cardAnimationDistance = getTop()
            val cardAnim = card.scaleAndTranslateY(0.5f, cardAnimationDistance * -1f, duration = DEFAULT_DURATION)
            toolbar.alpha = 0f
            val toolbarAnim = toolbar.fadeIn(DEFAULT_DURATION)
            title.alpha = 0f
            val titleAnim = title.fadeInAndTranslateY(title.measuredHeight * -1f, 0f, DEFAULT_DURATION)
            subtitle.alpha = 0f
            val subtitleAnim = subtitle.fadeInAndTranslateY(subtitle.measuredHeight * -1f, 0f, DEFAULT_DURATION)
            textField.alpha = 0f
            val textFieldAnim =
                textField.fadeInAndTranslateY(textField.measuredHeight.toFloat(), 0f, (DEFAULT_DURATION * 0.5).toLong())
            button.alpha = 0f
            val buttonAnim = button.fadeInAndTranslateY(button.measuredHeight.toFloat(), 0f, DEFAULT_DURATION)

            titleAnim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    card.translationY = 0f
                    listener?.onTitleAnimationEnd()
                }
            })

            AnimatorSet().apply {
                play(cardAnim)
                play(toolbarAnim).with(titleAnim).with(subtitleAnim).after((DEFAULT_DURATION * 1.33f).toLong())
                play(buttonAnim).after(toolbarAnim)
                play(textFieldAnim).after(buttonAnim)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        listener?.onAnimationEnd()
                    }
                })
                start()
            }
        }
    }

    fun prepareForExit() {
        ConstraintSet().apply {
            clone(parentView)
            connect(button.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, button.top)
            connect(textField.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, textField.top)
            clear(button.id, ConstraintSet.BOTTOM)
            clear(textField.id, ConstraintSet.BOTTOM)
            applyTo(parentView)
        }
    }

    fun playExit() {
        card.showFront()
        val cardAnim = card.scaleAndTranslateY(1f, cardAnimationDistance)
        val toolAnim = toolbar.fadeOut()
        val titleAnim = title.fadeOutAndTranslateY(title.measuredHeight * -1f)
        val subtitleAnim = subtitle.fadeOutAndTranslateY(subtitle.measuredHeight * -1f)
        val textFieldAnim = textField.fadeOutAndTranslateY(
            textField.measuredHeight.toFloat(), duration = (DEFAULT_DURATION * 0.5).toLong())
        val buttonAnim = button.translateY(getBottom())

        AnimatorSet().apply {
            play(cardAnim).with(toolAnim).with(titleAnim).with(subtitleAnim).with(textFieldAnim).with(buttonAnim)
            duration = DEFAULT_DURATION
            start()
        }
    }

    fun fromBundle(bundle: Bundle) {
        cardAnimationDistance = bundle.getFloat(EXTRA_ANIM_DISTANCE)
    }

    fun toBundle(bundle: Bundle) {
        bundle.putFloat(EXTRA_ANIM_DISTANCE, cardAnimationDistance)
    }

    private fun getTop(): Float {
        val params = card.layoutParams as ViewGroup.MarginLayoutParams
        return card.top.toFloat() - params.topMargin - title.bottom
    }

    private fun getBottom(): Float {
        return parentView.height - button.top - (button.layoutParams as ViewGroup.MarginLayoutParams).marginStart - button.height.toFloat()
    }

    interface Listener {
        fun onTitleAnimationEnd()
        fun onAnimationEnd()
    }
}