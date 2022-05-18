package com.mercadopago.android.px.internal.datasource

import android.content.Context
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.repository.ChargeRepository
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import java.math.BigDecimal
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ChargeRepositoryTest: BasicRobolectricTest() {

    private lateinit var chargeRepository: ChargeRepository

    private val amChargeFree = PaymentTypeChargeRule.createChargeFreeRule(PaymentTypes.ACCOUNT_MONEY, "test msg")
    private val debitCharge = PaymentTypeChargeRule.Builder(PaymentTypes.DEBIT_CARD, BigDecimal.TEN).build()

    @Before
    fun setUp() {
        chargeRepository =
            ChargeService(getContext().getSharedPreferences("com.mercadopago.checkout.store", Context.MODE_PRIVATE))
    }

    @Test
    fun testCustomChargesShouldReturnEmptyWhenConfiguredChargesAreEmpty() {
        chargeRepository.configure(emptyList())
        assertTrue(chargeRepository.customCharges.isEmpty())
    }

    @Test
    fun testCustomChargesShouldReturnChargesWhenConfiguredChargesAreNotEmpty() {
        chargeRepository.configure(listOf(amChargeFree, debitCharge))
        chargeRepository.customCharges.size.assertEquals(2)
        chargeRepository.customCharges.containsAll(listOf(amChargeFree, debitCharge))
    }

    @Test
    fun testCustomChargesResetShouldResetCustomChargesToEmptyList() {
        chargeRepository.configure(listOf(amChargeFree, debitCharge))
        chargeRepository.reset()
        assertTrue(chargeRepository.customCharges.isEmpty())
    }

    @Test
    fun testGetChargeAmountShouldReturnZeroWhenNoChargesForThatPaymentType() {
        chargeRepository.configure(listOf(amChargeFree, debitCharge))
        chargeRepository.getChargeAmount(PaymentTypes.CREDIT_CARD).assertEquals(BigDecimal.ZERO)
    }

    @Test
    fun testGetChargeAmountShouldReturnChargeAmountForThatPaymentType() {
        chargeRepository.configure(listOf(amChargeFree, debitCharge))
        chargeRepository.getChargeAmount(PaymentTypes.DEBIT_CARD).assertEquals(BigDecimal.TEN)
    }

    @Test
    fun testGetChargeShouldReturnNullWhenNoChargeForThatPaymentType() {
        chargeRepository.configure(listOf(amChargeFree, debitCharge))
        assertNull(chargeRepository.getChargeRule(PaymentTypes.CREDIT_CARD))
    }

    @Test
    fun testGetChargeShouldReturnChargeForThatPaymentType() {
        chargeRepository.configure(listOf(amChargeFree, debitCharge))
        with(chargeRepository.getChargeRule(PaymentTypes.DEBIT_CARD)) {
            assertNotNull(this)
            this!!.assertEquals(debitCharge)
        }
    }
}