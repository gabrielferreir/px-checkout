package com.mercadopago.android.px.internal.features.payment_result.instruction.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionInteraction

internal class InstructionInteractionAdapter(
    context: Context,
    models: List<InstructionInteraction.Model>,
    private val listener: InstructionActionAdapter.Listener
) : ArrayAdapter<InstructionInteraction.Model>(context, 0, models) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) = InstructionInteraction(context).apply {
        getItem(position)?.also { init(it, listener) }
    }
}
