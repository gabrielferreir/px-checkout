package com.mercadopago.android.px.internal.features.payment_result.instruction.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionReference

internal class InstructionReferenceAdapter(
    context: Context,
    models: List<InstructionReference.Model>
) : ArrayAdapter<InstructionReference.Model>(context, 0, models) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) = InstructionReference(context).apply {
        getItem(position)?.also { init(it) }
    }
}
