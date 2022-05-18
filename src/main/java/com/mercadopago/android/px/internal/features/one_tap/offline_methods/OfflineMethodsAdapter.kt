package com.mercadopago.android.px.internal.features.one_tap.offline_methods

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mercadopago.android.px.R
import java.util.*

internal class OfflineMethodsAdapter(private val onMethodSelectedListener: OfflineMethods.OnMethodSelectedListener)
    : RecyclerView.Adapter<OfflineMethodsRowHolder>() {

    private val offlineItems: MutableList<OfflineMethodItem> = ArrayList()
    private var lastHolder: OfflineMethodsRowHolder? = null
    private var lastPositionSelected = -1

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): OfflineMethodsRowHolder {
        val offlineMethodView = LayoutInflater.from(parent.context)
            .inflate(R.layout.px_view_offline_item, parent, false)
        return OfflineMethodsRowHolder(offlineMethodView)
    }

    override fun onBindViewHolder(holder: OfflineMethodsRowHolder, position: Int) {
        if(lastPositionSelected >= 0 && lastPositionSelected == position) {
            holder.setChecked(true)
            lastHolder = holder
        }
        holder.populate(offlineItems[position], object : OnItemClicked {
            override fun onClick() {
                lastHolder?.setChecked(false)
                holder.setChecked(true)
                lastHolder = holder
                lastPositionSelected = position
                onMethodSelectedListener.onItemSelected(offlineItems[position])
            }
        })
    }

    fun setItems(items: Collection<OfflineMethodItem>) {
        offlineItems.clear()
        offlineItems.addAll(items)
        offlineItems.firstOrNull { item -> !item.isOfflinePaymentTypeItem }?.run {
            lastPositionSelected = offlineItems.indexOf(this)
            onMethodSelectedListener.onItemSelected(this)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return offlineItems.size
    }

    internal interface OnItemClicked {
        fun onClick()
    }

}