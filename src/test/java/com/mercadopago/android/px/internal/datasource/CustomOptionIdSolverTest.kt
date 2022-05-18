package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.model.CardMetadata
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.BankTransfer
import com.mercadopago.android.px.model.internal.OneTapItem
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CustomOptionIdSolverTest {

    @Mock
    private lateinit var applicationSelectionRepositoryImpl: ApplicationSelectionRepositoryImpl

    @Mock
    private lateinit var oneTapItem: OneTapItem

    private lateinit var customOptionIdSolver: CustomOptionIdSolver

    @Before
    fun setUp() {
        customOptionIdSolver = CustomOptionIdSolverImpl(applicationSelectionRepositoryImpl)
    }

    @Test
    fun whenIsCardPaymentType() {
        val customOptionIdExpected = "123456"
        val card: CardMetadata = mock {
            on { id }.thenReturn(customOptionIdExpected)
        }
        val selectedPaymentMethod: Application.PaymentMethod = mock {
            on { this.type }.thenReturn("credit_card")
        }
        val application: Application = mock {
            on { this.paymentMethod }.thenReturn(selectedPaymentMethod)
        }
        whenever(oneTapItem.card).thenReturn(card)
        whenever(applicationSelectionRepositoryImpl[any<OneTapItem>()]).thenReturn(application)

        val actual = customOptionIdSolver[oneTapItem]

        customOptionIdExpected.assertEquals(actual)
    }

    @Test
    fun whenIsNotCardPaymentType() {
        val customOptionIdExpected = "account_money"
        val selectedPaymentMethod: Application.PaymentMethod = mock {
            on { id }.thenReturn(customOptionIdExpected)
            on { type }.thenReturn(customOptionIdExpected)
        }
        val application: Application = mock {
            on { this.paymentMethod }.thenReturn(selectedPaymentMethod)
        }
        whenever(applicationSelectionRepositoryImpl[any<OneTapItem>()]).thenReturn(application)

        val actual = customOptionIdSolver[oneTapItem]

        customOptionIdExpected.assertEquals(actual)
    }

    @Test
    fun whenIsOfflineMethod() {
        val customOptionIdExpected = "new_card_and_offline_payment_methods"
        val selectedPaymentMethod: Application.PaymentMethod = mock {
            on { type }.thenReturn(customOptionIdExpected)
        }
        val application: Application = mock {
            on { this.paymentMethod }.thenReturn(selectedPaymentMethod)
        }
        whenever(oneTapItem.isOfflineMethods).thenReturn(true)
        whenever(oneTapItem.getDefaultPaymentMethodType()).thenReturn("new_card_and_offline_payment_methods")
        whenever(applicationSelectionRepositoryImpl[any<OneTapItem>()]).thenReturn(application)

        val actual = customOptionIdSolver[oneTapItem]

        customOptionIdExpected.assertEquals(actual)
    }

    @Test
    fun whenGetCardCustomOptionIdByApplication() {
        val customOptionIdExpected = "123456"
        val card: CardMetadata = mock {
            on { id }.thenReturn(customOptionIdExpected)
        }
        val selectedPaymentMethod: Application.PaymentMethod = mock {
            on { type }.thenReturn("credit_card")
        }
        val application: Application = mock {
            on { this.paymentMethod }.thenReturn(selectedPaymentMethod)
        }
        whenever(oneTapItem.isCard).thenReturn(true)
        whenever(oneTapItem.card).thenReturn(card)

        val actual = CustomOptionIdSolver.getByApplication(oneTapItem, application)

        customOptionIdExpected.assertEquals(actual)
    }

    @Test
    fun whenGetAccountMoneyCustomOptionIdByApplication() {
        val customOptionIdExpected = "account_money"
        val selectedPaymentMethod: Application.PaymentMethod = mock {
            on { type }.thenReturn(customOptionIdExpected)
            on { id }.thenReturn(customOptionIdExpected)
        }
        val application: Application = mock {
            on { this.paymentMethod }.thenReturn(selectedPaymentMethod)
        }
        val actual = CustomOptionIdSolver.getByApplication(oneTapItem, application)

        customOptionIdExpected.assertEquals(actual)
    }

    @Test
    fun compareWhenOneTapItemIsCard() {
        val customOptionIdExpected = "123456"
        val card: CardMetadata = mock {
            on { id }.thenReturn(customOptionIdExpected)
        }

        whenever(oneTapItem.isCard).thenReturn(true)
        whenever(oneTapItem.card).thenReturn(card)

        assertTrue(CustomOptionIdSolver.compare(oneTapItem, "123456"))
    }

    @Test
    fun compareWhenOneTapItemIsAccountMoney() {
        val customOptionIdExpected = "account_money"

        whenever(oneTapItem.paymentMethodId).thenReturn(customOptionIdExpected)
        whenever(oneTapItem.isCard).thenReturn(false)

        assertTrue(CustomOptionIdSolver.compare(oneTapItem, "account_money"))
    }

    @Test
    fun compareWhenOneTapItemIsOfflineMethod() {
        val paymentMethodId = "new_card_and_offline_payment_methods"
        val customOptionIdExpected = "pago_facil"

        val paymentMethod: Application.PaymentMethod = mock {
            on { id }.thenReturn("pago_facil")
        }

        val application: Application = mock {
            on { this.paymentMethod }.thenReturn(paymentMethod)
        }

        whenever(oneTapItem.paymentMethodId).thenReturn(paymentMethodId)
        whenever(oneTapItem.isCard).thenReturn(false)
        whenever(oneTapItem.isOfflineMethods).thenReturn(true)
        whenever(oneTapItem.getApplications()).thenReturn(listOf(application))

        assertTrue(CustomOptionIdSolver.compare(oneTapItem, customOptionIdExpected))
    }

    @Test
    fun compareWhenOneTapItemIsBankTransfer() {
        val customOptionIdExpected = "123456"

        val bankTransfer: BankTransfer = mock {
            on { id }.thenReturn(customOptionIdExpected)
        }

        whenever(oneTapItem.bankTransfer).thenReturn(bankTransfer)
        whenever(oneTapItem.isBankTransfer()).thenReturn(true)

        assertTrue(CustomOptionIdSolver.compare(oneTapItem, customOptionIdExpected))
    }
}