package com.mercadopago.android.px.internal.mapper

import android.graphics.Color
import com.meli.android.carddrawer.configuration.CardDrawerStyle
import com.meli.android.carddrawer.configuration.FontType
import com.meli.android.carddrawer.configuration.SecurityCodeLocation
import com.meli.android.carddrawer.model.CardDrawerSource
import com.meli.android.carddrawer.model.GenericPaymentMethod
import com.mercadopago.android.px.internal.mappers.CardUiMapper
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.viewmodel.PaymentCard
import com.mercadopago.android.px.model.AccountMoneyDisplayInfo
import com.mercadopago.android.px.model.CardDisplayInfo
import com.mercadopago.android.px.model.internal.BankTransfer
import com.mercadopago.android.px.model.internal.OfflineMethodCard
import com.mercadopago.android.px.model.internal.Text
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CardUIMapperTest {

    @Test
    fun whenTagIsSpecifiedMapAccountMoneyShouldReturnCardDrawerPaymentCardWithTag() {
        val accountMoneyDisplayInfo = JsonUtil.fromJson("""{
            "slider_title": "test",
            "payment_method_image_url": "paymentMethodImageUrl",
            "type": "default",
            "color": "color",
            "message": "test",
            "gradient_colors": []
        }""".trimIndent(), AccountMoneyDisplayInfo::class.java)

        val cardTag = getCardTag()

        val expectedResult = with(accountMoneyDisplayInfo!!){
            PaymentCard(
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
                with(cardTag!!) { getTag() },
                gradientColors,
                style = CardDrawerStyle.ACCOUNT_MONEY_DEFAULT
            )
        }

        val actualResult = CardUiMapper.map(accountMoneyDisplayInfo, cardTag)

        Assert.assertTrue(ReflectionEquals(actualResult).matches(expectedResult))
        Assert.assertTrue(ReflectionEquals(actualResult).matches(expectedResult))
    }

    @Test
    fun whenTagIsSpecifiedMapPaymentCardShouldReturnCardDrawerPaymentCardWithTag() {
        val cardDisplayInfo = JsonUtil.fromJson("""{
            "payment_method_image": "paymentMethodImage",
            "payment_method_image_url": "paymentMethodImageUrl",
            "card_pattern": [1, 2, 3, 4],
            "cardholder_name": "cardholderName",
            "color": "color",
            "expiration": "expiration",
            "font_color": "fontColor",
            "font_type": "fontType",
            "issuer_id": 1234,
            "issuer_image": "issuerImage",
            "issuer_image_url": "issuerImageUrl",
            "last_four_digits": "7890",
            "security_code": {
                "card_location": "back",
                "length": "3"
            }
        }""".trimIndent(), CardDisplayInfo::class.java)


        val cardTag = getCardTag()

        val expectedResult = with(cardDisplayInfo!!){
            PaymentCard(
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
                with(cardTag!!) {getTag()},
                null,
                CardDrawerStyle.REGULAR
            )
        }

        val actualResult = CardUiMapper.map(cardDisplayInfo, cardTag)
        Assert.assertTrue(ReflectionEquals(actualResult).matches(expectedResult))
        Assert.assertTrue(ReflectionEquals(actualResult).matches(expectedResult))
    }

    @Test
    fun whenNoSubtitleSpecifiedMapOfflineMethodCardShouldReturnGenericPaymentMethodWithNoSubtitle() {
        val offlineMethodCardDisplayInfo = getOfflineMethodCardDisplayInfoWithoutSubtitle()
        val expectedResult = with(offlineMethodCardDisplayInfo!!) {
            GenericPaymentMethod(Color.parseColor(color),
                GenericPaymentMethod.Text(title.message, Color.parseColor(title.textColor)),
                paymentMethodImageUrl,
                null
            )
        }

        val actualResult = CardUiMapper.map(offlineMethodCardDisplayInfo, null)
        Assert.assertTrue(ReflectionEquals(actualResult).matches(expectedResult))
    }

    @Test
    fun whenNoTagSpecifiedMapOfflineMethodCardShouldReturnGenericPaymentMethodWithNoTag() {
        val offlineMethodCardDisplayInfo = getOfflineMethodCardDisplayInfoWithoutSubtitle()
        val expectedResult = with(offlineMethodCardDisplayInfo!!) {
            GenericPaymentMethod(Color.parseColor(color),
                GenericPaymentMethod.Text(title.message, Color.parseColor(title.textColor)),
                paymentMethodImageUrl
            )
        }

        val actualResult = CardUiMapper.map(offlineMethodCardDisplayInfo, null)
        Assert.assertTrue(ReflectionEquals(actualResult).matches(expectedResult))
    }

    @Test
    fun whenSubtitleIsSpecifiedMapOfflineMethodCardShouldReturnGenericPaymentMethodWithSubtitle() {
        val offlineMethodCardDisplayInfo = getOfflineMethodCardDisplayInfoWithSubtitle()
        val expectedResult = with(offlineMethodCardDisplayInfo!!) {
            GenericPaymentMethod(Color.parseColor(color),
                GenericPaymentMethod.Text(title.message, Color.parseColor(title.textColor)),
                paymentMethodImageUrl,
                subtitle!!.let{
                    GenericPaymentMethod.Text(it.message, Color.parseColor(it.textColor))
                }
            )
        }

        val actualResult = CardUiMapper.map(offlineMethodCardDisplayInfo, null)
        Assert.assertTrue(ReflectionEquals(actualResult).matches(expectedResult))
    }

    @Test
    fun whenTagIsSpecifiedMapOfflineMethodCardShouldReturnGenericPaymentMethodWithTag() {
        val offlineMethodCardDisplayInfo = getOfflineMethodCardDisplayInfoWithoutSubtitle()

        val cardTag = getCardTag()

        val expectedResult = with(offlineMethodCardDisplayInfo!!) {
            with(cardTag!!){
                GenericPaymentMethod(Color.parseColor(color),
                    GenericPaymentMethod.Text(title.message, Color.parseColor(title.textColor)),
                    paymentMethodImageUrl,
                    null,
                    getTag()
                )
            }
        }

        val actualResult = CardUiMapper.map(offlineMethodCardDisplayInfo, cardTag)
        Assert.assertTrue(ReflectionEquals(actualResult).matches(expectedResult))
    }

    @Test
    fun whenNoTagSpecifiedMapBankTransferShouldReturnGenericPaymentMethodWithNoTag() {
        val bankTransferDisplayInfo = getBankTransferDisplayInfo()
        val expectedResult = with(bankTransferDisplayInfo!!) {
            GenericPaymentMethod(
                Color.parseColor(color),
                GenericPaymentMethod.Text(title.message, Color.parseColor(title.textColor), title.weight),
                paymentMethodImageUrl,
                GenericPaymentMethod.Text(subtitle!!.message, Color.parseColor(subtitle!!.textColor), subtitle!!.weight),
                null,
                gradientColor,
                GenericPaymentMethod.Text(description!!.message, Color.parseColor(description!!.textColor), description!!.weight)
            )
        }

        val actualResult = CardUiMapper.map(bankTransferDisplayInfo, null)
        Assert.assertTrue(ReflectionEquals(actualResult).matches(expectedResult))
    }

    @Test
    fun whenTagIsSpecifiedMapBankTransferShouldReturnGenericPaymentMethodWithTag() {
        val bankTransferDisplayInfo = getBankTransferDisplayInfo()
        val cardTag = getCardTag()
        val expectedResult = with(bankTransferDisplayInfo!!) {
            GenericPaymentMethod(
                Color.parseColor(color),
                GenericPaymentMethod.Text(title.message, Color.parseColor(title.textColor), title.weight),
                paymentMethodImageUrl,
                GenericPaymentMethod.Text(subtitle!!.message, Color.parseColor(subtitle!!.textColor), subtitle!!.weight),
                cardTag!!.getTag(),
                gradientColor,
                GenericPaymentMethod.Text(description!!.message, Color.parseColor(description!!.textColor), description!!.weight)
            )
        }

        val actualResult = CardUiMapper.map(bankTransferDisplayInfo, cardTag)
        Assert.assertTrue(ReflectionEquals(actualResult).matches(expectedResult))
    }

    private fun getBankTransferDisplayInfo(): BankTransfer.DisplayInfo? {
        return JsonUtil.fromJson("""{
                "color": "#E7401A",
				"title": {
					"message": "Banco de la Nacion Argentina",
					"text_color": "#CC000000",
					"weight": "bold"
				},
				"subtitle": {
					"message": "Caja de ahorro",
					"text_color": "#00A650",
					"weight": "regular"
				},
				"description": {
					"message": "CBU: ***7429",
					"text_color": "#00A650",
					"weight": "regular"
				},
				"slider_title": "Vas a usar débito inmediato",
				"payment_method_image_url": "https://http2.mlstatic.com/storage/logos-api-admin/ab4d1990-a9df-11eb-b106-b5150396fcef-xl@2x.png",
				"gradient_color": [
					"#FFFFFF",
					"#FE4040"
				]
            }""".trimIndent(), BankTransfer.DisplayInfo::class.java)
    }

    private fun getCardTag(): Text? {
        return JsonUtil.fromJson("""{
                "message": "Novo",
                "text_color": "#009ee2",
                "background_color": "#FFFFFF",
                "weight": "bold"
            }""".trimIndent(), Text::class.java)
    }

    // Helpers so we don't duplicate this code

    private fun Text.getTag() =
        CardDrawerSource.Tag(message, Color.parseColor(backgroundColor), Color.parseColor(textColor), weight ?: "regular")

    private fun getOfflineMethodCardDisplayInfoWithSubtitle(): OfflineMethodCard.DisplayInfo? {
        return JsonUtil.fromJson("""{
                "color": "#FFFFFF",
                "payment_method_image_url": "https://mobile.mercadolibre.com/remote_resources/image/card_drawer_mlb_pm_pix_normal?density=xhdpi&locale=es_AR&version=1",
                "title": {
                    "message": "PIX",
                    "text_color": "#CC000000",
                    "weight": "regular"
                },
                "subtitle": {
                    "message": "Aprovação Imediata",
                    "text_color": "#00A650",
                    "weight": "regular"
                }
            }""".trimIndent(), OfflineMethodCard.DisplayInfo::class.java)
    }

    private fun getOfflineMethodCardDisplayInfoWithoutSubtitle(): OfflineMethodCard.DisplayInfo? {
        return JsonUtil.fromJson("""{
                "color": "#FFFFFF",
                "payment_method_image_url": "https://mobile.mercadolibre.com/remote_resources/image/card_drawer_mlb_pm_pix_normal?density=xhdpi&locale=es_AR&version=1",
                "title": {
                    "message": "PIX",
                    "text_color": "#CC000000",
                    "weight": "regular"
                }
            }""".trimIndent(), OfflineMethodCard.DisplayInfo::class.java)
    }
}
