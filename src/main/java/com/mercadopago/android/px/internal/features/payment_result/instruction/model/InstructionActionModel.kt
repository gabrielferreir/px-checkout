package com.mercadopago.android.px.internal.features.payment_result.instruction.model

internal sealed class InstructionActionModel {
    data class Link(
        val label: String,
        val url: String
    ) : InstructionActionModel()

    data class Copy(
        val label: String,
        val content: String
    ) : InstructionActionModel()
}
