package com.mercadopago.android.px.internal.features.payment_result.instruction.mapper

import android.text.TextUtils
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.Instruction
import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionReference.Model as ReferenceModel

internal class InstructionReferenceMapper : Mapper<Instruction, List<ReferenceModel>?>() {

    override fun map(value: Instruction): List<ReferenceModel>? {
        return value.references?.map {
            var spacesFound = 0
            var title: String? = null
            val info = value.info
            if (info != null) {
                for (text in info) {
                    if (text.isEmpty()) {
                        spacesFound++
                    } else if (spacesFound == 2) {
                        title = text
                        break
                    }
                }
            }
            ReferenceModel(title, it.label, getFormattedReference(it.fieldValue, it.separator), it.comment)
        }
    }

    private fun getFormattedReference(fieldValue: List<String>?, separator: String?): String {
        val stringBuilder = StringBuilder()
        if (fieldValue != null) {
            for (string in fieldValue) {
                stringBuilder.append(string)
                if (fieldValue.indexOf(string) != fieldValue.size - 1 && !TextUtils.isEmpty(separator)) {
                    stringBuilder.append(separator)
                }
            }
        }
        return stringBuilder.toString()
    }
}
