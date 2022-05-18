package com.mercadopago.android.px.internal.features.one_tap.offline_methods

import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.presentation.extensions.loadOrGone
import com.mercadopago.android.px.internal.extensions.gone
import com.mercadopago.android.px.internal.extensions.visible
import com.mercadopago.android.px.internal.features.one_tap.offline_methods.OfflineMethodsAdapter.OnItemClicked
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.view.MPTextView

internal class OfflineMethodsRowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val paymentTypeName: MPTextView = itemView.findViewById(R.id.payment_type_name)
    private val paymentMethodName: MPTextView = itemView.findViewById(R.id.payment_method_name)
    private val description: MPTextView = itemView.findViewById(R.id.description)
    private val methodImageView: ImageView = itemView.findViewById(R.id.method_image)
    private val radioButton: RadioButton = itemView.findViewById(R.id.radio_button)

    fun populate(offlineItem: OfflineMethodItem, onItemClicked: OnItemClicked) {
        if (offlineItem.isOfflinePaymentTypeItem) {
            ViewUtils.loadOrHide(View.GONE, offlineItem.name, paymentTypeName)
            paymentMethodName.gone()
            description.gone()
            methodImageView.gone()
            radioButton.gone()
            itemView.setOnClickListener(null)
        } else {
            paymentTypeName.gone()
            ViewUtils.loadOrHide(View.GONE, offlineItem.name, paymentMethodName)
            ViewUtils.loadOrHide(View.GONE, offlineItem.description, description)
            methodImageView.loadOrGone(offlineItem.imageUrl)
            radioButton.visible()
            itemView.setOnClickListener { onItemClicked.onClick() }
        }
    }

    fun setChecked(checked: Boolean) {
        radioButton.isChecked = checked
    }
}