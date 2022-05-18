package com.mercadopago.android.px.internal.features.payment_result.instruction

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.features.payment_result.instruction.adapter.InstructionActionAdapter
import com.mercadopago.android.px.internal.features.payment_result.instruction.model.InstructionActionModel
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.view.AdapterLinearLayout
import com.mercadopago.android.px.internal.view.MPTextView

internal class InstructionInteraction @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.px_payment_result_instruction_interaction, this)
        orientation = VERTICAL
    }

    private val title: MPTextView = findViewById(R.id.title)
    private val content: MPTextView = findViewById(R.id.content)
    private val actions: AdapterLinearLayout = findViewById(R.id.actions)

    fun init(model: Model, listener: InstructionActionAdapter.Listener) {
        ViewUtils.loadLikeHtmlOrGone(model.title, title)
        ViewUtils.loadLikeHtmlOrGone(model.content, content)
        if (model.showMultilineContent == false) {
            content.maxLines = 1
            content.ellipsize = TextUtils.TruncateAt.END
        }
        model.action?.let { actions.setAdapter(InstructionActionAdapter(context, listOf(it), listener)) }
    }

    data class Model (
        val title: String,
        val content: String?,
        val showMultilineContent: Boolean?,
        val action: InstructionActionModel?
    )
}
