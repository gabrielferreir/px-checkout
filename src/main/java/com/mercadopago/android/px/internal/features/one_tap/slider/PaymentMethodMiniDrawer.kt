package com.mercadopago.android.px.internal.features.one_tap.slider

import androidx.fragment.app.Fragment
import com.mercadopago.android.px.internal.features.one_tap.add_new_card.OtherPaymentMethodLowResFragment
import com.mercadopago.android.px.internal.features.one_tap.slider.CardMiniFragment.Companion.getInstance
import com.mercadopago.android.px.internal.features.one_tap.slider.ConsumerCreditsMiniFragment.Companion.getInstance
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem
import com.mercadopago.android.px.internal.viewmodel.drawables.OtherPaymentMethodFragmentItem
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodFragmentDrawer

internal class PaymentMethodMiniDrawer : PaymentMethodFragmentDrawer {
    override fun draw(drawableFragmentItem: DrawableFragmentItem): Fragment {
        return getInstance(drawableFragmentItem)
    }

    override fun draw(drawableItem: OtherPaymentMethodFragmentItem): Fragment {
        return OtherPaymentMethodLowResFragment.getInstance(drawableItem)
    }

    override fun draw(drawableItem: ConsumerCreditsDrawableFragmentItem): Fragment {
        return getInstance(drawableItem)
    }
}