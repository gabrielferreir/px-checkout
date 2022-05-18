package com.mercadopago.android.px.internal.features.payment_result.instruction.mapper

import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionInfo
import com.mercadopago.android.px.internal.mappers.Mapper

internal class InstructionInfoMapper : Mapper<List<String>, InstructionInfo.Model>() {

    override fun map(value: List<String>): InstructionInfo.Model {
        val title: String?
        val content: String?
        with(value) {
            if (hasNestedTitle(this)) {
                title = this[0]
                content = if (size > 2) slice(2 until size).joinToString("<br>") else null
            } else {
                title = null
                content = slice(0 until size).joinToString("<br>")
            }
        }
        return InstructionInfo.Model(title, content)
    }

    private fun hasNestedTitle(info: List<String>) = info.run { size == 1 || size > 1 && this[1].isEmpty() }
}
