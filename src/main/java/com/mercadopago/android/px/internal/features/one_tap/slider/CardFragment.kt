package com.mercadopago.android.px.internal.features.one_tap.slider

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.meli.android.carddrawer.configuration.CardDrawerStyle
import com.meli.android.carddrawer.model.CardDrawerView
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem
import com.mercadopago.android.px.model.PaymentTypes
import com.meli.android.carddrawer.model.PaymentCard as CardDrawerPaymentCard

internal open class CardFragment : PaymentMethodFragment<DrawableFragmentItem>() {
    private lateinit var cardView: CardDrawerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_fragment_card, container, false)
    }

    override fun updateCardDrawerView(cardDrawerView: CardDrawerView) {
        cardView = cardDrawerView
        with(cardView) {
            model.commonsByApplication.getCurrent().cardDrawerConfiguration?.let { configuration ->
                configuration.paymentCard?.let {
                    card.name = it.name
                    card.expiration = it.date
                    card.number = it.number
                    if (it.style != CardDrawerStyle.REGULAR) {
                        cardView.id = R.id.px_card_account_money
                    }
                    // To show tag we have to create the CardDrawerPaymentCard
                    show(CardDrawerPaymentCard(it, it.getTag()))
                } ?: configuration.genericPaymentMethod?.let {
                    show(it)
                }
            }
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        }
    }

    override fun getAccessibilityContentDescription(): String {
        val builder = SpannableStringBuilder()
        with(model.commonsByApplication.getCurrent()) {
            when {
                PaymentTypes.isAccountMoney(cardDrawerConfiguration?.paymentMethodId) -> builder
                    .append(description)
                else -> builder
                    .append(cardDrawerConfiguration?.paymentMethodId)
                    .append(TextUtil.SPACE)
                    .append(issuerName)
                    .append(TextUtil.SPACE)
                    .append(description)
                    .append(TextUtil.SPACE)
                    .append(getString(R.string.px_date_divider))
                    .append(TextUtil.SPACE)
                    .append(cardDrawerConfiguration?.paymentCard?.name)
            }
        }
        return builder.toString()
    }

    override fun disable() {
        super.disable()
        cardView.isEnabled = false
    }

    override fun enable() {
        super.enable()
        cardView.isEnabled = true
    }

    companion object {
        @JvmStatic
        fun getInstance(model: DrawableFragmentItem) = CardFragment().also {
            it.storeModel(model)
        }
    }
}
