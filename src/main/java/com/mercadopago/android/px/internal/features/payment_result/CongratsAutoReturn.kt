package com.mercadopago.android.px.internal.features.payment_result

import android.os.CountDownTimer
import android.os.Parcelable
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.util.TextUtil
import kotlinx.android.parcel.Parcelize
import kotlin.math.ceil

private const val ALL = "all"
private const val APPROVED = "approved"
private const val SECONDS = 5L
private const val TIME_PATTERN = "00:00"

internal class CongratsAutoReturn(model: Model, listener: Listener) {

    private val timer = object : CountDownTimer((model.seconds?.toLong() ?: SECONDS) * 1000, 1000) {
        override fun onFinish() = listener.onFinish()
        override fun onTick(millisUntilFinished: Long) {
            if (model.label.isNotNullNorEmpty()) {
                listener.updateView(getUpdatedLabel(model.label, millisUntilFinished))
            }
        }
    }

    fun start() {
        timer.start()
    }

    fun cancel() {
        timer.cancel()
    }

    private fun getUpdatedLabel(label: String, millisUntilFinished: Long): String {
        val secondsToShow = ceil(millisUntilFinished / 1000f).toInt().toString()
        return TextUtil.format(label, "${TIME_PATTERN.dropLast(secondsToShow.length)}$secondsToShow")
    }

    @Parcelize
    data class Model(
        val label: String? = null,
        val seconds: Int? = null
    ) : Parcelable

    interface Listener {
        fun onFinish()
        fun updateView(label: String)
    }

    companion object {
        fun isValid(value: String?) = value == ALL || value == APPROVED
    }
}
