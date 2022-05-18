package com.mercadopago.android.px.internal.features.payment_result.instruction

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.view.MPTextView

internal class InstructionReference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.px_payment_result_instruction_reference, this)
        orientation = VERTICAL
    }

    private val title: MPTextView = findViewById(R.id.title)
    private val label: MPTextView = findViewById(R.id.label)
    private val reference: MPTextView = findViewById(R.id.reference)
    private val comment: MPTextView = findViewById(R.id.comment)

    fun init(model: Model) {
        ViewUtils.loadLikeHtmlOrGone(model.title, title)
        ViewUtils.loadLikeHtmlOrGone(model.label, label)
        ViewUtils.loadLikeHtmlOrGone(model.reference, reference)
        ViewUtils.loadLikeHtmlOrGone(model.comment, comment)
    }

    data class Model (
        val title: String?,
        val label: String,
        val reference: String,
        val comment: String?
    )
}
