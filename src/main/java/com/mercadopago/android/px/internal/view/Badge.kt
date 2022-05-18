package com.mercadopago.android.px.internal.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.mercadolibre.android.picassodiskcache.PicassoDiskLoader
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.presentation.extensions.setDrawableBackgroundColor
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.model.internal.Text

class Badge(context: Context, attrs: AttributeSet?, defStyleAttr: Int): LinearLayout(context, attrs, defStyleAttr) {

    init {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.px_badge_view, this)
        orientation = HORIZONTAL
        setBackgroundResource(R.drawable.px_background_badge)
    }

    fun setText(text: Text) {
        findViewById<MPTextView>(R.id.text).setText(text)
        setBadgeBackgroundColor(text.backgroundColor)
    }

    private fun setBadgeBackgroundColor(color: String?) {
        setDrawableBackgroundColor(color)
    }

    fun setIconUrl(iconUrl: String?) {
        if (iconUrl.isNotNullNorEmpty()) {
            PicassoDiskLoader.get(context).load(iconUrl).into(findViewById<ImageView>(R.id.icon))
        }
    }
}