package com.mercadopago.android.px.internal.viewmodel

import android.graphics.Color
import android.os.Parcelable
import com.meli.android.carddrawer.configuration.CardDrawerStyle
import com.meli.android.carddrawer.configuration.FontType
import com.meli.android.carddrawer.model.CardAnimationType
import com.meli.android.carddrawer.model.CardDrawerSource
import com.meli.android.carddrawer.model.CardUI
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.util.TextUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
internal class PaymentCard(
    val name: String,
    val date: String,
    val number: String,
    private val issuerImageUrl: String?,
    private val paymentMethodImageUrl: String?,
    private val fontType: String?,
    private val cardPattern: IntArray,
    private val color: String,
    private val fontColor: String?,
    private val securityCodeLocation: String,
    private val securityCodeLength: Int,
    private val tag: CardDrawerSource.Tag?,
    private val gradientColors: List<String>? = null,
    private val style: CardDrawerStyle) : CardUI, Parcelable {

    override fun getBankImageRes() = 0

    override fun getBankImageUrl() = issuerImageUrl

    override fun getCardLogoImageUrl() = paymentMethodImageUrl

    override fun getAnimationType() = CardAnimationType.NONE

    override fun getFontType() = fontType ?: FontType.LIGHT_TYPE

    override fun getCardLogoImageRes() = 0

    override fun getSecurityCodePattern() = securityCodeLength

    override fun getExpirationPlaceHolder() = TextUtil.EMPTY

    override fun getSecurityCodeLocation() = securityCodeLocation

    override fun getCardNumberPattern() = cardPattern

    override fun getNamePlaceHolder() = TextUtil.EMPTY

    override fun getCardBackgroundColor() = Color.parseColor(color)

    override fun getCardFontColor() = if (fontColor.isNotNullNorEmpty()) Color.parseColor(fontColor) else 0

    override fun getCardGradientColors() = gradientColors

    override fun getStyle() = style

    fun getTag(): CardDrawerSource.Tag? = tag
}
