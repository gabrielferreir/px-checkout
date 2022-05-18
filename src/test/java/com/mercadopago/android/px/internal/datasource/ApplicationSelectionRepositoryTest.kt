package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.internal.repository.OneTapItemRepository
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.CardDrawerSwitch
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.model.one_tap.SliderDisplayInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ApplicationSelectionRepositoryTest {

    @Mock
    private lateinit var fileManager: FileManager

    @Mock
    private lateinit var oneTapItemRepository: OneTapItemRepository

    @Mock
    private lateinit var oneTapItem: OneTapItem

    @Mock
    private lateinit var creditCardApplication: Application

    @Mock
    private lateinit var accountMoneyApplication: Application

    @Mock
    private lateinit var creditCardPaymentMethod: Application.PaymentMethod

    @Mock
    private lateinit var accountMoneyPaymentMethod: Application.PaymentMethod

    @Mock
    private lateinit var displayInfo: SliderDisplayInfo

    @Mock
    private lateinit var cardDrawerSwitch: CardDrawerSwitch

    private lateinit var applicationSelectionRepository: ApplicationSelectionRepository

    @Before
    fun setUp() {
        applicationSelectionRepository = ApplicationSelectionRepositoryImpl(fileManager, oneTapItemRepository)

        whenever(oneTapItemRepository["123456"]).thenReturn(oneTapItem)
        whenever(oneTapItem.displayInfo).thenReturn(displayInfo)
        whenever(oneTapItem.id).thenReturn("123456")
        whenever(displayInfo.cardDrawerSwitch).thenReturn(cardDrawerSwitch)
        whenever(cardDrawerSwitch.default).thenReturn("account_money")
        whenever(oneTapItem.getApplications()).thenReturn(listOf(accountMoneyApplication, creditCardApplication))
        whenever(creditCardApplication.paymentMethod).thenReturn(creditCardPaymentMethod)
        whenever(accountMoneyApplication.paymentMethod).thenReturn(accountMoneyPaymentMethod)
        whenever(creditCardPaymentMethod.id).thenReturn("visa")
        whenever(creditCardPaymentMethod.type).thenReturn("credit_card")
        whenever(accountMoneyPaymentMethod.id).thenReturn("account_money")
        whenever(accountMoneyPaymentMethod.type).thenReturn("account_money")
    }

    @Test
    fun getDefaultApplicationBySwitchDefaultSelection() {
        val currentApplication = applicationSelectionRepository["123456"]

        currentApplication.paymentMethod.id.assertEquals("account_money")
        currentApplication.paymentMethod.type.assertEquals("account_money")
    }

    @Test
    fun getDefaultApplicationWhitOutSwitchDefaultSelection() {
        whenever(displayInfo.cardDrawerSwitch).thenReturn(null)

        val currentApplication = applicationSelectionRepository["123456"]

        currentApplication.paymentMethod.id.assertEquals("account_money")
        currentApplication.paymentMethod.type.assertEquals("account_money")
    }

    @Test
    fun updateApplicationSelection() {
        applicationSelectionRepository[oneTapItem] = creditCardApplication
        val currentApplication = applicationSelectionRepository["123456"]

        currentApplication.paymentMethod.id.assertEquals("visa")
        currentApplication.paymentMethod.type.assertEquals("credit_card")
    }

}