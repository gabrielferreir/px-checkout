package com.mercadopago.android.px.internal.features.one_tap.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem

internal class CardLowResFragment : CardFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_fragment_card_low_res, container, false)
    }

    companion object {
        @JvmStatic
        fun getInstance(model: DrawableFragmentItem) = CardLowResFragment().also {
            it.storeModel(model)
        }
    }
}