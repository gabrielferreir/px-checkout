package com.mercadopago.android.px.internal.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import com.mercadopago.android.px.R
import java.util.*
import kotlin.math.min


class PulseView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : RelativeLayout(context, attrs, defStyleAttr) {
    private var pulseColor = 0
    private var pulseStrokeWidth = 0f
    private var pulseRadius = 0f
    private var pulseDurationTime = 0
    private var pulseAmount = 0
    private var pulseDelay = 0
    private var pulseScale = 0f
    private var pulseType = 0
    private var isRippleAnimationRunning = false
    private lateinit var animatorSet: AnimatorSet
    private lateinit var animatorList: ArrayList<Animator>
    private lateinit var pulseParams: LayoutParams
    private val pulseViewList = ArrayList<RippleView>()
    private var hasShowAnimation = true

    init {
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private fun init(context: Context, attrs: AttributeSet?) {
        if (isInEditMode) return
        requireNotNull(attrs) { "Attributes should not be null" }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PulseView)
        pulseColor = typedArray.getColor(R.styleable.PulseView_pulse_color, ContextCompat.getColor(context, R.color.pulseColor))
        pulseStrokeWidth = typedArray.getDimension(R.styleable.PulseView_pulse_strokeWidth, resources.getDimension(R.dimen.pulseStrokeWidth))
        pulseRadius = typedArray.getDimension(R.styleable.PulseView_pulse_radius, resources.getDimension(R.dimen.pulseRadius))
        pulseDurationTime = typedArray.getInt(R.styleable.PulseView_pulse_duration, DEFAULT_DURATION_TIME)
        pulseAmount = typedArray.getInt(R.styleable.PulseView_pulse_rippleAmount, DEFAULT_RIPPLE_COUNT)
        pulseScale = typedArray.getFloat(R.styleable.PulseView_pulse_scale, DEFAULT_SCALE)
        pulseType = typedArray.getInt(R.styleable.PulseView_pulse_type, DEFAULT_FILL_TYPE)
        typedArray.recycle()

        pulseDelay = pulseDurationTime / pulseAmount

        val paint = Paint()
        paint.isAntiAlias = true

        if (pulseType == DEFAULT_FILL_TYPE) {
            pulseStrokeWidth = 0f
            paint.style = Paint.Style.FILL
        } else {
            paint.style = Paint.Style.STROKE
        }

        paint.color = pulseColor

        pulseParams = LayoutParams((2 * (pulseRadius + pulseStrokeWidth)).toInt(), (2 * (pulseRadius + pulseStrokeWidth)).toInt())
        pulseParams.addRule(CENTER_IN_PARENT, TRUE)
        animatorSet = AnimatorSet()
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorList = ArrayList()

        for (i in 0 until pulseAmount) {
            val pulseView = RippleView(getContext(), paint)
            addView(pulseView, pulseParams)
            pulseViewList.add(pulseView)
            val scaleXAnimator = ObjectAnimator.ofFloat(pulseView, "ScaleX", 1.0f, pulseScale)
            scaleXAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleXAnimator.repeatMode = ObjectAnimator.REVERSE
            scaleXAnimator.startDelay = i * pulseDelay.toLong()
            scaleXAnimator.duration = pulseDurationTime.toLong()
            animatorList.add(scaleXAnimator)
            val scaleYAnimator = ObjectAnimator.ofFloat(pulseView, "ScaleY", 1.0f, pulseScale)
            scaleYAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleYAnimator.repeatMode = ObjectAnimator.REVERSE
            scaleYAnimator.startDelay = i * pulseDelay.toLong()
            scaleYAnimator.duration = pulseDurationTime.toLong()
            animatorList.add(scaleYAnimator)
        }
        animatorSet.playTogether(animatorList)
        isSaveEnabled = true
    }

    private inner class RippleView(context: Context?, paint: Paint) : View(context) {
        private var paint: Paint
        init {
            visibility = INVISIBLE
            this.paint = paint
        }

        override fun onDraw(canvas: Canvas) {
            val radius = min(width, height) / 2
            canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius - pulseStrokeWidth, paint)
        }
    }

    fun startRippleAnimation() {
        if (!isRippleAnimationRunning) {
            for (pulseView in pulseViewList) {
                pulseView.visibility = View.VISIBLE
            }
            animatorSet.start()
            isRippleAnimationRunning = true
        }
    }

    fun stopRippleAnimation() {
        if (isRippleAnimationRunning) {
            animatorSet.end()
            isRippleAnimationRunning = false
        }
        for (pulseView in pulseViewList) {
            pulseView.visibility = View.GONE
        }
        hasShowAnimation = false
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val pulseState = state as PulseViewState
        super.onRestoreInstanceState(pulseState.superState)
        hasShowAnimation = pulseState.hasShowAnimation()
        if (!hasShowAnimation) {
            stopRippleAnimation()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val pulseState = PulseViewState(super.onSaveInstanceState())
        pulseState.setHasShowAnimation(hasShowAnimation)
        return pulseState
    }

    internal class PulseViewState : BaseSavedState {
        private var showAnimation = true

        constructor(source: Parcel) : super(source) {
            showAnimation = source.readByte() != 0.toByte()
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByte((if (showAnimation) 1 else 0).toByte())
        }

        fun setHasShowAnimation(showAnimation: Boolean) {
            this.showAnimation = showAnimation
        }
        fun hasShowAnimation() = showAnimation

        companion object CREATOR : Parcelable.Creator<PulseViewState?> {
            override fun createFromParcel(`in`: Parcel) = PulseViewState(`in`)
            override fun newArray(size: Int) = arrayOfNulls<PulseViewState?>(size)
        }
    }

    companion object {
        private const val DEFAULT_RIPPLE_COUNT = 6
        private const val DEFAULT_DURATION_TIME = 3000
        private const val DEFAULT_SCALE = 6.0f
        private const val DEFAULT_FILL_TYPE = 0
    }
}