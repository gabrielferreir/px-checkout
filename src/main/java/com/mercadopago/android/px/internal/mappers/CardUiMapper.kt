package com.mercadopago.android.px.internal.mappers

import android.graphics.Color
import com.meli.android.carddrawer.configuration.CardDrawerStyle
import com.meli.android.carddrawer.configuration.FontType
import com.meli.android.carddrawer.configuration.SecurityCodeLocation
import com.meli.android.carddrawer.model.GenericPaymentMethod
import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessCardDisplayInfo
import com.mercadopago.android.px.internal.font.PxFont
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.viewmodel.PaymentCard
import com.mercadopago.android.px.model.AccountMoneyDisplayInfo
import com.mercadopago.android.px.model.AccountMoneyDisplayInfoType
import com.mercadopago.android.px.model.CardDisplayInfo
import com.mercadopago.android.px.model.CardDisplayInfoType
import com.mercadopago.android.px.model.internal.BankTransfer
import com.mercadopago.android.px.model.internal.OfflineMethodCard
import com.mercadopago.android.px.model.internal.Text
import java.util.Locale
import com.meli.android.carddrawer.model.GenericPaymentMethod.Text as CardDrawerText
import com.meli.android.carddrawer.model.CardDrawerSource.Tag as CardDrawerTag

internal object CardUiMapper {

    fun map(cardDisplayInfo: BusinessCardDisplayInfo): PaymentCard {
        with(cardDisplayInfo) {
            return PaymentCard(
                cardholderName,
                expiration,
                cardPatternMask,
                issuerImageUrl,
                paymentMethodImageUrl,
                fontType,
                cardPattern,
                color,
                fontColor,
                securityCodeLocation,
                securityCodeLength,
                null,
                null,
                mapCardDisplayInfoTypeToCardDrawerStyle(type)
            )
        }
    }

    fun map(cardDisplayInfo: CardDisplayInfo, cardTag: Text?): PaymentCard {
        with(cardDisplayInfo) {
            return PaymentCard(
                cardholderName,
                expiration,
                getCardPattern(),
                issuerImageUrl,
                paymentMethodImageUrl,
                fontType,
                cardPattern,
                color,
                fontColor,
                securityCode.cardLocation,
                securityCode.length,
                mapCardTagToCardDrawerTag(cardTag),
                null,
                mapCardDisplayInfoTypeToCardDrawerStyle(type)
            )
        }
    }

    fun map(accountMoneyDisplayInfo: AccountMoneyDisplayInfo, cardTag: Text?): PaymentCard {
        with(accountMoneyDisplayInfo) {
            return PaymentCard(
                TextUtil.EMPTY,
                TextUtil.EMPTY,
                TextUtil.EMPTY,
                null,
                paymentMethodImageUrl,
                FontType.NONE,
                intArrayOf(),
                color,
                null,
                SecurityCodeLocation.NONE,
                0,
                mapCardTagToCardDrawerTag(cardTag),
                gradientColors,
                mapAccountMoneyDisplayInfoTypeToCardDrawerStyle(type)
            )
        }
    }

    fun map(displayInfo: OfflineMethodCard.DisplayInfo, cardTag: Text?): GenericPaymentMethod {
        with(displayInfo) {
            return GenericPaymentMethod(
                Color.parseColor(color),
                CardDrawerText(title.message, Color.parseColor(title.textColor)),
                paymentMethodImageUrl,
                subtitle?.let {
                    CardDrawerText(it.message, Color.parseColor(it.textColor))
                },
                mapCardTagToCardDrawerTag(cardTag),
                gradientColor
            )
        }
    }

    fun map(displayInfo: BankTransfer.DisplayInfo, cardTag: Text?): GenericPaymentMethod {
        with(displayInfo) {
            return GenericPaymentMethod(
                Color.parseColor(color),
                CardDrawerText(title.message, Color.parseColor(title.textColor), title.weight),
                paymentMethodImageUrl,
                subtitle?.let {
                    CardDrawerText(it.message, Color.parseColor(it.textColor), it.weight)
                },
                mapCardTagToCardDrawerTag(cardTag),
                gradientColor,
                description?.let {
                    CardDrawerText(it.message, Color.parseColor(it.textColor), it.weight)
                }
            )
        }
    }

    private fun mapCardTagToCardDrawerTag(cardTag : Text?) : CardDrawerTag? {
        return cardTag?.let {
            CardDrawerTag(cardTag.message, Color.parseColor(cardTag.backgroundColor),
                Color.parseColor(cardTag.textColor), cardTag.weight ?: PxFont.REGULAR.name.toLowerCase(Locale.ROOT))
        }
    }

    private fun mapAccountMoneyDisplayInfoTypeToCardDrawerStyle(type: AccountMoneyDisplayInfoType?): CardDrawerStyle {
       return when(type) {
           AccountMoneyDisplayInfoType.HYBRID -> CardDrawerStyle.ACCOUNT_MONEY_HYBRID
            else -> CardDrawerStyle.ACCOUNT_MONEY_DEFAULT
        }
    }

    private fun mapCardDisplayInfoTypeToCardDrawerStyle(type: CardDisplayInfoType?): CardDrawerStyle {
        return when(type) {
            CardDisplayInfoType.HYBRID -> CardDrawerStyle.ACCOUNT_MONEY_HYBRID
            else -> CardDrawerStyle.REGULAR
        }
    }
}
