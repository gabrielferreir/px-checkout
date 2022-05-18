package com.mercadopago.android.px.internal.viewmodel

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.font.PxFont

internal class SummaryRowTextDescriptor (
    val text: ILocalizedCharSequence,
    @ColorRes val textColor: Int = R.color.px_expressCheckoutTextColor,
    val font: PxFont = PxFont.REGULAR,
    @DimenRes val textSize: Int = R.dimen.px_s_text
) : ITextDescriptor {
    override fun getText(context: Context): CharSequence = text.get(context)
    override fun getFont(context: Context) = font
    override fun getTextColor(context: Context) = ContextCompat.getColor(context, textColor)
    override fun getTextSize(context: Context) = context.resources.getDimension(textSize)
}
