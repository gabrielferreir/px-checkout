package com.mercadopago.android.px.internal.features.payment_result.instruction.mapper

import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.Instruction
import com.mercadopago.android.px.internal.features.payment_result.instruction.Instruction.Model as InstructionModel

internal class InstructionMapper(
    private val infoMapper: InstructionInfoMapper,
    private val interactionMapper: InstructionInteractionMapper,
    private val referenceMapper: InstructionReferenceMapper,
    private val actionMapper: InstructionActionMapper
) : Mapper<Instruction, InstructionModel>() {

    override fun map(value: Instruction) = value.run {
        InstructionModel(
            subtitle,
            info?.let { infoMapper.map(it) },
            interactions?.let { interactionMapper.map(it) },
            references?.let { referenceMapper.map(this) },
            actions?.let { actionMapper.map(it) },
            secondaryInfo?.joinToString("<br>"),
            tertiaryInfo?.joinToString("<br>"),
            accreditationComments?.joinToString("<br>"),
            accreditationMessage
        )
    }
}
