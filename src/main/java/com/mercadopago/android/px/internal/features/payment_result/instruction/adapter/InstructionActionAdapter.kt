package com.mercadopago.android.px.internal.features.payment_result.instruction.adapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import com.mercadolibre.android.ui.widgets.MeliButton
import com.mercadopago.android.px.internal.actions.CopyAction
import com.mercadopago.android.px.internal.actions.LinkAction
import com.mercadopago.android.px.internal.features.payment_result.instruction.model.InstructionActionModel
import android.view.ViewGroup.LayoutParams

internal class InstructionActionAdapter(
    context: Context,
    models: List<InstructionActionModel>,
    val listener: Listener
) : ArrayAdapter<InstructionActionModel>(context, 0, models) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return when(val model = getItem(position)!!) {
            is InstructionActionModel.Copy -> renderButton(model.label) {
                listener.onCopyAction(CopyAction(model.content))
            }
            is InstructionActionModel.Link -> renderButton(model.label) {
                listener.onLinkAction(LinkAction(model.url))
            }
        }
    }

    private fun renderButton(label: String, listener: (View) -> Unit): AppCompatButton {
        return MeliButton(context).also {
            it.type = MeliButton.Type.OPTION_PRIMARY
            it.text = label
            it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            it.setOnClickListener(listener)
        }
    }

    interface Listener {
        fun onLinkAction(action: LinkAction)
        fun onCopyAction(action: CopyAction)
    }
}
