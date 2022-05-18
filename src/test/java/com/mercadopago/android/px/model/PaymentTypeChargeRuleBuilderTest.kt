package com.mercadopago.android.px.model

import android.content.Context
import android.os.Parcel
import androidx.fragment.app.DialogFragment
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.core.DynamicDialogCreator
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner::class)
class PaymentTypeChargeRuleBuilderTest {

    @Test(expected = IllegalArgumentException::class)
    fun testBuilderWithZeroAmountShouldThrowException() {
        PaymentTypeChargeRule.Builder(PaymentTypes.ACCOUNT_MONEY, BigDecimal.ZERO)
    }

    @Test
    fun testBuilderWithNonZeroAmountShouldBuildWithoutAnyOtherProperty() {
        val charge = PaymentTypeChargeRule.Builder(PaymentTypes.ACCOUNT_MONEY, BigDecimal.TEN).build()
        charge.charge().assertEquals(BigDecimal.TEN)
        charge.paymentTypeId.assertEquals(PaymentTypes.ACCOUNT_MONEY)
        assertFalse(charge.hasDetailModal())
        assertNull(charge.label)
        assertNull(charge.message)
    }

    @Test
    fun testBuilderWithNonZeroAmountShouldBuildWithTaxableTrueAsDefault() {
        val charge = PaymentTypeChargeRule.Builder(PaymentTypes.ACCOUNT_MONEY, BigDecimal.TEN).build()
        assertTrue(charge.taxable)
    }

    @Test
    fun testDeprecatedConstructorShouldHaveTaxableTrueAsDefault() {
        val charge = PaymentTypeChargeRule(PaymentTypes.ACCOUNT_MONEY, BigDecimal.TEN)
        assertTrue(charge.taxable)
    }

    @Test
    fun testBuilderSetTaxableShouldSetTaxable() {
        val charge = PaymentTypeChargeRule.Builder(PaymentTypes.ACCOUNT_MONEY, BigDecimal.TEN)
            .setTaxable(false)
            .build()
        assertFalse(charge.taxable)
    }

    @Test
    fun testBuilderSetLabelShouldSetLabel() {
        val charge = PaymentTypeChargeRule.Builder(PaymentTypes.ACCOUNT_MONEY, BigDecimal.TEN)
            .setLabel("Test label")
            .build()
        assertNotNull(charge.label)
        charge.label?.assertEquals("Test label")
    }

    @Test
    fun testBuilderSetModalShouldSetModal() {
        val charge = PaymentTypeChargeRule.Builder(PaymentTypes.ACCOUNT_MONEY, BigDecimal.TEN)
            .setDetailModal(object : DynamicDialogCreator {
                override fun shouldShowDialog(
                    context: Context,
                    checkoutData: DynamicDialogCreator.CheckoutData
                ): Boolean {
                    return true
                }

                override fun create(context: Context, checkoutData: DynamicDialogCreator.CheckoutData): DialogFragment {
                    return DialogFragment()
                }

                override fun describeContents(): Int {
                    return 0
                }

                override fun writeToParcel(parcel: Parcel, i: Int) {}
            })
            .build()
        assertNotNull(charge.detailModal)
    }
}