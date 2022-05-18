package com.mercadopago.android.px.securitycode

import com.mercadopago.android.px.internal.features.security_code.domain.use_case.SecurityTrackModelUseCase
import com.mercadopago.android.px.internal.features.security_code.mapper.TrackingParamModelMapper
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.Issuer
import com.mercadopago.android.px.model.PaymentMethod
import com.mercadopago.android.px.tracking.internal.model.Reason
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.internal.matchers.apachecommons.ReflectionEquals

class TrackingParamModelMapperTest {

    @Test
    fun whenTrackingParamModelMapper() {
        val cardMock = mock(Card::class.java)
        val reasonMock = mock(Reason::class.java)
        val issuerMock = mock(Issuer::class.java)
        val paymentMethodMock = mock(PaymentMethod::class.java)

        `when`(cardMock.id).thenReturn("123")
        `when`(cardMock.issuer).thenReturn(issuerMock)
        `when`(issuerMock.id).thenReturn(1234)
        `when`(cardMock.paymentMethod).thenReturn(paymentMethodMock)
        `when`(paymentMethodMock.id).thenReturn("visa")
        `when`(paymentMethodMock.paymentTypeId).thenReturn("credit_card")
        `when`(cardMock.firstSixDigits).thenReturn("123456")

        val expectedResult = SecurityTrackModelUseCase.SecurityTrackModelParams(SecurityTrackModelUseCase.CardTrackParams(
            cardMock.id.orEmpty(),
            cardMock.paymentMethod!!.id,
            cardMock.paymentMethod!!.paymentTypeId,
            cardMock.issuer!!.id,
            cardMock.firstSixDigits.orEmpty()), reasonMock)

        val actualResult = TrackingParamModelMapper().map(cardMock, reasonMock)
        assertTrue(ReflectionEquals(actualResult.card).matches(expectedResult.card))
        assertTrue(ReflectionEquals(actualResult.reason).matches(expectedResult.reason))
    }
}
