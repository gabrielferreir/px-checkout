package com.mercadopago.android.px.internal.features.one_tap.slider

import androidx.fragment.app.Fragment
import com.mercadopago.android.px.internal.features.one_tap.add_new_card.OtherPaymentMethodDynamicFragment
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem
import com.mercadopago.android.px.internal.viewmodel.drawables.OtherPaymentMethodFragmentItem
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodFragmentDrawer

class PaymentMethodDynamicDrawer : PaymentMethodFragmentDrawer {
    override fun draw(drawableFragmentItem: DrawableFragmentItem): Fragment {
        return CardDynamicFragment.getInstance(drawableFragmentItem)
    }

    override fun draw(drawableItem: OtherPaymentMethodFragmentItem): Fragment {
        return OtherPaymentMethodDynamicFragment.getInstance(drawableItem)
    }

    override fun draw(drawableItem: ConsumerCreditsDrawableFragmentItem): Fragment {
        return ConsumerCreditsDynamicFragment.getInstance(drawableItem)
    }
}