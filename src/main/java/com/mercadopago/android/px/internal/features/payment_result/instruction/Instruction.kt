package com.mercadopago.android.px.internal.features.payment_result.instruction

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.extensions.visible
import com.mercadopago.android.px.internal.features.payment_result.instruction.adapter.InstructionActionAdapter
import com.mercadopago.android.px.internal.features.payment_result.instruction.adapter.InstructionInteractionAdapter
import com.mercadopago.android.px.internal.features.payment_result.instruction.adapter.InstructionReferenceAdapter
import com.mercadopago.android.px.internal.features.payment_result.instruction.model.InstructionActionModel
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.view.AdapterLinearLayout
import com.mercadopago.android.px.internal.view.MPTextView

internal class Instruction @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.px_payment_result_instructions, this)
        orientation = VERTICAL
    }

    private val subtitle: MPTextView = findViewById(R.id.instruction_subtitle)
    private val info: InstructionInfo = findViewById(R.id.instruction_info)
    private val interactions: AdapterLinearLayout = findViewById(R.id.instruction_interactions)
    private val references: AdapterLinearLayout = findViewById(R.id.instruction_references)
    private val actions: AdapterLinearLayout = findViewById(R.id.instruction_actions)
    private val secondaryInfo: MPTextView = findViewById(R.id.instruction_secondary_info)
    private val tertiaryInfo: MPTextView = findViewById(R.id.instruction_tertiary_info)
    private val accreditationComments: MPTextView = findViewById(R.id.instruction_accreditation_comments)
    private val accreditationTime: MPTextView = findViewById(R.id.instruction_accreditation_time)

    fun init(model: Model, listener: InstructionActionAdapter.Listener) {
        ViewUtils.loadLikeHtmlOrGone(model.subtitle, subtitle)
        model.info?.let {
            info.visible()
            info.init(it)
        }
        model.interactions?.let {
            interactions.visible()
            interactions.setAdapter(InstructionInteractionAdapter(context, it, listener))
        }
        model.references?.let {
            references.visible()
            references.setAdapter(InstructionReferenceAdapter(context, it))
        }
        model.actions?.let {
            actions.visible()
            actions.setAdapter(InstructionActionAdapter(context, it, listener))
        }
        ViewUtils.loadLikeHtmlOrGone(model.secondaryInfo, secondaryInfo)
        ViewUtils.loadLikeHtmlOrGone(model.tertiaryInfo, tertiaryInfo)
        ViewUtils.loadLikeHtmlOrGone(model.accreditationComments, accreditationComments)
        ViewUtils.loadLikeHtmlOrGone(model.accreditationTime, accreditationTime)
    }

    data class Model(
        val subtitle: String?,
        val info: InstructionInfo.Model?,
        val interactions: List<InstructionInteraction.Model>?,
        val references: List<InstructionReference.Model>?,
        val actions: List<InstructionActionModel>?,
        val secondaryInfo: String?,
        val tertiaryInfo: String?,
        val accreditationComments: String?,
        val accreditationTime: String?
    )
}
