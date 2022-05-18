package com.mercadopago.android.px.internal.view

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.widget.AppCompatTextView
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.commons.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.core.presentation.extensions.setColor
import com.mercadopago.android.px.core.presentation.extensions.setTextColor
import com.mercadopago.android.px.internal.extensions.toGravity
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import com.mercadopago.android.px.internal.font.FontHelper.setFont
import com.mercadopago.android.px.internal.font.PxFont
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.viewmodel.ITextDescriptor
import com.mercadopago.android.px.model.internal.Text
import com.mercadopago.android.px.model.internal.TextAlignment

internal class MPTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MPTextView)
        val font = PxFont.from(a.getInt(R.styleable.MPTextView_customStyle, PxFont.REGULAR.id))
        a.recycle()
        if (!isInEditMode) {
            setFont(this, font)
        }
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                configureEllipsize()
            }
        })
    }

    fun loadTextListOrGone(textDescriptors: List<ITextDescriptor>?) {
        if (textDescriptors.isNullOrEmpty()) {
            visibility = View.GONE
            return
        }
        val spannableStringBuilder = SpannableStringBuilder()
        var startIndex = 0
        var endIndex: Int
        textDescriptors.forEach { textDescriptor ->
            spannableStringBuilder.append(textDescriptor.getText(context)).takeUnless {
                textDescriptor == textDescriptors.last()
            }?.append(TextUtil.SPACE)
            endIndex = spannableStringBuilder.length
            ViewUtils.setFontInSpannable(
                context,
                textDescriptor.getFont(context),
                spannableStringBuilder,
                startIndex,
                endIndex
            )
            spannableStringBuilder.setColor(
                textDescriptor.getTextColor(context),
                startIndex,
                endIndex
            )
            startIndex = spannableStringBuilder.length
            textDescriptor.getTextSize(context)?.let {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
            }
        }
        text = spannableStringBuilder
        visibility = View.VISIBLE
    }

    fun loadOrGone(textDescriptor: ITextDescriptor) {
        return this.loadTextListOrGone(listOf(textDescriptor))
    }

    fun setText(text: Text) {
        setText(text.message, text.textColor, text.weight, text.alignment)
    }

    fun setText(text: PaymentCongratsText) {
        setText(text.message, text.textColor, text.weight, text.alignment, text.fontSize)
    }

    private fun setText(message: String?, textColor: String?, weight: String?, alignment: TextAlignment?, fontSize: Float? = null) {
        text = message
        setTextColor(textColor)
        fontSize?.let {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, it)
        }
        if (weight.isNotNullNorEmpty()) {
            setFont(this, PxFont.from(weight!!))
        }
        gravity = alignment.toGravity()

    }

    private fun configureEllipsize() {
        val truncateAt = ellipsize
        if (truncateAt != null && truncateAt == TruncateAt.END && lineCount > maxLines) {
            val indexLastLine = layout.getLineEnd(maxLines - 1)
            val text = text.subSequence(0, indexLastLine - 3).toString() + "..."
            setText(text)
        }
    }
}
