package com.mercadopago.android.px.internal.datasource

import android.util.MalformedJsonException
import com.mercadopago.android.px.addons.model.ThreeDSDataOnlyParams
import com.mercadopago.android.px.internal.repository.CardHolderAuthenticatorRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.services.CardHolderAuthenticatorService
import com.mercadopago.android.px.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner::class)
class CardHolderAuthenticatorRepositoryTest {

    @Mock
    private lateinit var cardHolderAuthenticatorService: CardHolderAuthenticatorService

    @Mock
    private lateinit var paymentSettingRepository: PaymentSettingRepository

    private lateinit var cardHolderAuthenticatorRepository: CardHolderAuthenticatorRepository

    @Before
    fun setUp() {
        cardHolderAuthenticatorRepository = CardHolderAuthenticatorRepositoryImpl(
            cardHolderAuthenticatorService, paymentSettingRepository)
    }

    @Test(expected = IllegalStateException::class)
    fun whenTokenIsNullThenThrowException() {
        runBlocking {
            cardHolderAuthenticatorRepository.authenticate(makePaymentData().createPaymentData(), mock())
        }
    }

    @Test(expected = MalformedJsonException::class)
    fun whenSdkEphemeralPublicKeyIsInvalidThenThrowException()  {
        runBlocking {
            cardHolderAuthenticatorRepository.authenticate(makePaymentData().setToken(mock()).createPaymentData(), mock())
        }
    }

    @Test
    fun whenHasTokenAndSdkEphemeralPublicKeyThenAuthenticate() {
            val cardHolder: Cardholder = mock {
                on { name }.thenReturn("pepe perez")
            }
            val token: Token = mock {
                on { id }.thenReturn("654321")
                on { this.cardHolder }.thenReturn(cardHolder)
            }
            val site: Site = mock {
                on { id }.thenReturn("MLA")
            }
            val threeDSDataOnlyParams: ThreeDSDataOnlyParams = mock {
                on { sdkEphemeralPublicKey }.thenReturn("{\"kty\":\"\",\"crv\":\"\",\"x\":\"\",\"y\":\"\"}")
                on { sdkAppId }.thenReturn("")
                on { deviceData }.thenReturn("")
                on { sdkReferenceNumber }.thenReturn("")
                on { sdkTransactionId }.thenReturn("")
            }

            whenever(paymentSettingRepository.currency).thenReturn(mock())

            whenever(paymentSettingRepository.site).thenReturn(site)

        runBlocking {

            whenever(cardHolderAuthenticatorService.authenticate(any(), any())).thenReturn(mock())

            assertNotNull(cardHolderAuthenticatorRepository.authenticate(
                makePaymentData().setToken(token).createPaymentData(),
                threeDSDataOnlyParams))
            verify(cardHolderAuthenticatorService).authenticate(any(), any())
        }
    }

    private fun makePaymentData(): PaymentData.Builder {
        val paymentMethod = mock<PaymentMethod> {
            on { id }.thenReturn("visa")
        }

        return PaymentData.Builder()
            .setRawAmount(BigDecimal.TEN)
            .setNoDiscountAmount(BigDecimal.TEN)
            .setPaymentMethod(paymentMethod)
    }
}