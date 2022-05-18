package com.mercadopago.android.px.internal.features.payment_result.instruction.mapper

import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.InstructionAction
import com.mercadopago.android.px.internal.features.payment_result.instruction.model.InstructionActionModel

internal class InstructionActionMapper : Mapper<InstructionAction, InstructionActionModel>() {

    override fun map(values: Iterable<InstructionAction>): List<InstructionActionModel> {
        return values.filter { it.tag == InstructionAction.Tag.LINK }.map {
            map(it)
        }
    }

    override fun map(value: InstructionAction): InstructionActionModel {
        return when(value.tag) {
            InstructionAction.Tag.LINK -> InstructionActionModel.Link(value.label, value.url!!)
            InstructionAction.Tag.COPY -> InstructionActionModel.Copy(value.label, value.content!!)
        }
    }
}
