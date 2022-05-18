package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.internal.features.one_tap.slider.PaymentMethodFragment
import com.mercadopago.android.px.internal.features.one_tap.slider.PaymentMethodHighResDrawer
import com.mercadopago.android.px.internal.features.one_tap.slider.PaymentMethodLowResDrawer
import com.mercadopago.android.px.internal.features.one_tap.slider.PaymentMethodMiniDrawer
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem
import com.mercadopago.android.px.model.internal.remedies.CardSize

internal class RemediesPaymentMethodMapper(
    private val cardSize: CardSize?
) : Mapper<DrawableFragmentItem, PaymentMethodFragment<*>>() {

    override fun map(drawableFragmentItem: DrawableFragmentItem): PaymentMethodFragment<*> {
        return when (cardSize) {
            CardSize.LARGE -> {
                PaymentMethodHighResDrawer()
            }
            CardSize.SMALL,
            CardSize.XSMALL -> {
                PaymentMethodLowResDrawer()
            }
            CardSize.MINI -> {
                PaymentMethodMiniDrawer()
            }
            else -> PaymentMethodLowResDrawer()
        }.let {
            drawableFragmentItem.draw(it) as PaymentMethodFragment<*>
        }
    }
}
