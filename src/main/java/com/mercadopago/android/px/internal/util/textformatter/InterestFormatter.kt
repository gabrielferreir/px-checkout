package com.mercadopago.android.px.internal.util.textformatter

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import com.mercadopago.android.px.core.presentation.extensions.setColor
import com.mercadopago.android.px.internal.font.PxFont
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.model.internal.Text

internal open class InterestFormatter(private val spannableStringBuilder: SpannableStringBuilder) {

    @ColorInt
    private var textColor: Int? = null
    private var font: PxFont = PxFont.REGULAR
    private lateinit var text: String

    fun withText(text: Text) = apply {
        this.text = text.message ?: TextUtil.EMPTY
        this.font = PxFont.from(text.weight)
        try {
            if (this.text.isNotEmpty()) {
                this.textColor = Color.parseColor(text.textColor)
            }
        } catch (e: IllegalArgumentException) {
        }
        return this
    }

    fun apply(context: Context) {
        if (text.isNotEmpty()) {
            val indexStart = spannableStringBuilder.length
            spannableStringBuilder.append(" ").append(text)
            val indexEnd = spannableStringBuilder.length

            updateTextColor(indexStart, indexEnd)
            updateTextFont(context, indexStart, indexEnd)
        }
    }

    private fun updateTextColor(indexStart: Int, indexEnd: Int) {
        textColor?.let {
            spannableStringBuilder.setColor(it, indexStart, indexEnd)
        }
    }

    private fun updateTextFont(context: Context, indexStart: Int, indexEnd: Int) {
        ViewUtils.setFontInSpannable(context, font, spannableStringBuilder, indexStart, indexEnd)
    }
}
