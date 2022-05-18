package com.mercadopago.android.px.internal.features.payment_result.instruction.mapper

import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionInteraction
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.Interaction

internal class InstructionInteractionMapper(
    private val actionMapper: InstructionActionMapper
) : Mapper<Interaction, InstructionInteraction.Model>() {

    override fun map(value: Interaction) = value.run {
        InstructionInteraction.Model(
            title,
            content,
            showMultilineContent,
            action?.let { actionMapper.map(it) }
        )
    }
}
