package com.mercadopago.android.px.internal.features.one_tap.slider

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.mercadopago.android.px.internal.features.one_tap.RenderMode
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodFragmentDrawer

internal class PaymentMethodFragmentAdapter @JvmOverloads constructor(
    fragment: Fragment,
    private val renderMode: RenderMode = RenderMode.HIGH_RES
) : FragmentStateAdapter(fragment) {

    private var items: List<DrawableFragmentItem> = emptyList()
    private val drawer = initDrawer()

    fun getItems() = items

    fun setItems(items: List<DrawableFragmentItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun createFragment(position: Int): Fragment = items[position].draw(drawer)

    override fun getItemCount() = items.size

    private fun initDrawer(): PaymentMethodFragmentDrawer {
        return when (renderMode) {
            RenderMode.LOW_RES -> PaymentMethodLowResDrawer()
            RenderMode.DYNAMIC -> PaymentMethodDynamicDrawer()
            else -> PaymentMethodHighResDrawer()
        }
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
       (holder.itemView as ViewGroup).clipChildren = false
       super.onBindViewHolder(holder, position, payloads)
    }
}
