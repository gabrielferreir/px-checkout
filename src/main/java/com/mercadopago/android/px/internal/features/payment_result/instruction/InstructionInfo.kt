package com.mercadopago.android.px.internal.features.payment_result.instruction

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.view.MPTextView

internal class InstructionInfo @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.px_payment_result_instructions_info, this)
        orientation = VERTICAL
    }

    private val title: MPTextView = findViewById(R.id.title)
    private val content: MPTextView = findViewById(R.id.content)

    fun init(model: Model) {
        ViewUtils.loadLikeHtmlOrGone(model.title, title)
        ViewUtils.loadLikeHtmlOrGone(model.content, content)
    }

    data class Model(
        val title: String?,
        val content: String?
    )
}
