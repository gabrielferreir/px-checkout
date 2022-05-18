package com.mercadopago.android.px.internal.features.one_tap.add_new_card.sheet_options

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.extensions.loadOrElse
import com.mercadopago.android.px.internal.extensions.visible
import com.mercadopago.android.px.internal.view.MPTextView
import com.mercadopago.android.px.model.CardFormInitType
import com.mercadopago.android.px.model.internal.CardFormOption

internal class SheetOptionsRecyclerViewAdapter(
    private val cardFormOptions: List<CardFormOption>,
    private val cardFormOptionSelected: (CardFormInitType) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SheetOptionViewHolder(inflateViewHolderView(parent, R.layout.sheet_option))
    }

    override fun getItemCount() = cardFormOptions.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sheetOptionData = cardFormOptions[position]
        with(holder as SheetOptionViewHolder) {
            sheetOptionImage.loadOrElse(sheetOptionData.imageUrl, R.drawable.px_generic_method)
            sheetOptionTitle.setText(sheetOptionData.title)
            sheetOptionData.subtitle?.let {
                with(sheetOptionSubTitle) {
                    visible()
                    setText(it)
                }
            }
            itemView.setOnClickListener {
                cardFormOptionSelected(sheetOptionData.cardFormInitType)
            }
        }
    }

    private fun inflateViewHolderView(parent: ViewGroup, layoutResId: Int) = LayoutInflater
        .from(parent.context)
        .inflate(layoutResId, parent, false)
}

internal class SheetOptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val sheetOptionImage: ImageView = view.findViewById(R.id.sheet_option_image)
    val sheetOptionTitle: MPTextView = view.findViewById(R.id.sheet_option_title)
    val sheetOptionSubTitle: MPTextView = view.findViewById(R.id.sheet_option_subtitle)
}