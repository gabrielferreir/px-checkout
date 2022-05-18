package com.mercadopago.android.px.internal.util

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.PaymentMethod
import com.mercadopago.android.px.model.exceptions.CardTokenException
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TokenCreationWrapperTest {

    private lateinit var tokenCreationWrapper: TokenCreationWrapper

    @Mock private lateinit var cardTokenRepository: CardTokenRepository
    @Mock private lateinit var escManagerBehaviour: ESCManagerBehaviour
    @Mock private lateinit var card: Card
    @Mock private lateinit var paymentMethod: PaymentMethod

    @Before
    fun setUp() {
        tokenCreationWrapper = TokenCreationWrapper.Builder(cardTokenRepository, escManagerBehaviour)
            .with(card)
            .with(paymentMethod).build()
    }

    @Test
    fun whenCVVisValidWithoutTokenThenPassesValidation() {
        assertTrue(tokenCreationWrapper.validateCVVFromToken("123"))
    }

    @Test(expected = CardTokenException::class)
    fun whenCVVisInvalidWithoutTokenThenFailsValidation() {
        tokenCreationWrapper.validateCVVFromToken("12345")
    }
}