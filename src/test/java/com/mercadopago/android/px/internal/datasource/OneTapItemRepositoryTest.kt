package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.OneTapItemRepository
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey
import com.mercadopago.android.px.model.CardMetadata
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod
import com.mercadopago.android.px.model.internal.OneTapItem
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class OneTapItemRepositoryTest {

    @Mock
    private lateinit var fileManager: FileManager

    @Mock
    private lateinit var disabledPaymentMethodRepository: DisabledPaymentMethodRepository

    @Mock
    private lateinit var cardOneTapItem: OneTapItem

    @Mock
    private lateinit var accountMoneyOneTapItem: OneTapItem

    private lateinit var oneTapItemRepository: OneTapItemRepository
    private lateinit var oneTapItems: List<OneTapItem>
    private val cardCustomOptionId = "visa"
    private val accountMoneyCustomOptionId = "account_money"

    @Before
    fun setUp() {
        oneTapItems =  listOf(accountMoneyOneTapItem, cardOneTapItem)
        oneTapItemRepository = OneTapItemRepositoryImpl(fileManager, disabledPaymentMethodRepository)

        val card: CardMetadata = mock {
            on { id }.thenReturn(cardCustomOptionId)
        }

        val cardPaymentMethod: Application.PaymentMethod = mock {
            on { type }.thenReturn("credit_card")
        }

        val cardApplication: Application = mock {
            on { this.paymentMethod }.thenReturn(cardPaymentMethod)
        }

        val accountMoneyPaymentMethod: Application.PaymentMethod = mock {
            on { id }.thenReturn(accountMoneyCustomOptionId)
            on { type }.thenReturn(accountMoneyCustomOptionId)
        }

        val accountMoneyApplication: Application = mock {
            on { this.paymentMethod }.thenReturn(accountMoneyPaymentMethod)
        }

        whenever(cardOneTapItem.isCard).thenReturn(true)
        whenever(cardOneTapItem.card).thenReturn(card)
        whenever(cardOneTapItem.getApplications()).thenReturn(listOf(cardApplication))
        whenever(accountMoneyOneTapItem.paymentMethodId).thenReturn(accountMoneyCustomOptionId)
        whenever(accountMoneyOneTapItem.getApplications()).thenReturn(listOf(accountMoneyApplication))

        oneTapItemRepository.configure(oneTapItems)
    }

    @Test
    fun whenGetCardOneTapItem() {
        val actual = oneTapItemRepository[cardCustomOptionId]

        cardOneTapItem.assertEquals(actual)
    }

    @Test
    fun whenGetNoCardOneTapItem() {
        val actual = oneTapItemRepository[accountMoneyCustomOptionId]

        accountMoneyOneTapItem.assertEquals(actual)
    }

    @Test
    fun whenSortByState() {
        val card: CardMetadata = mock {
            on { id }.thenReturn("master")
        }
        val paymentMethod: Application.PaymentMethod = mock {
            on { type }.thenReturn("credit_card")
        }
        val application: Application = mock {
            on { this.paymentMethod }.thenReturn(paymentMethod)
        }
        val disableItem: OneTapItem = mock {
            on { isCard }.thenReturn(true)
            on { this.card }.thenReturn(card)
            on { getApplications() }.thenReturn(listOf(application))
        }

        val disablePaymentMethod: DisabledPaymentMethod = mock()

        whenever(disabledPaymentMethodRepository.value).thenReturn(mutableMapOf(
            PayerPaymentMethodKey("master", "credit_card") to disablePaymentMethod
        ))
        val actual = mutableListOf(disableItem, accountMoneyOneTapItem, cardOneTapItem)
        val expected = listOf(accountMoneyOneTapItem, cardOneTapItem, disableItem)

        oneTapItemRepository.configure(actual)

        oneTapItemRepository.sortByState()

        expected.forEachIndexed { index, oneTapItem ->
            assertTrue(
                CustomOptionIdSolver.compare(oneTapItem, CustomOptionIdSolver.defaultCustomOptionId(actual[index])))
        }
    }
}
