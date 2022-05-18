package com.mercadopago.android.px.internal.mapper

import android.content.Context
import android.os.Parcel
import androidx.fragment.app.DialogFragment
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.core.DynamicDialogCreator
import com.mercadopago.android.px.internal.mappers.CustomChargeToPaymentTypeChargeMapper
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import com.mercadopago.android.px.model.internal.CustomChargeDM
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner::class)
class CustomChargeToPaymentTypeChargeMapperTest {

    @Mock
    private lateinit var paymentConfiguration: PaymentConfiguration

    private lateinit var mapper: CustomChargeToPaymentTypeChargeMapper

    @Before
    fun setUp() {
        mapper = CustomChargeToPaymentTypeChargeMapper(paymentConfiguration)
    }

    @Test
    fun whenNoCustomChargesProvidedThenItShouldReturnAnEmptyList() {
        val customChargesMap: Map<String, CustomChargeDM> = mapOf()
        val result = mapper.map(customChargesMap)
        result.size.assertEquals(0)
    }

    @Test
    fun whenOnlyOneCustomChargeIsProvidedAndThereAreNoCurrentChargesThenItShouldReturnCustomCharge() {
        whenever(paymentConfiguration.charges).thenReturn(arrayListOf())
        val customChargesMap: Map<String, CustomChargeDM> = mapOf(
            getAMCustomCharge()
        )
        val result = mapper.map(customChargesMap)
        val expectedResult = listOf(
            PaymentTypeChargeRule("account_money", BigDecimal.TEN, null, "Impuesto")
        )
        result.size.assertEquals(expectedResult.size)
        assertTrue(ReflectionEquals(result.first()).matches(expectedResult.first()))
    }

    @Test
    fun whenOneCustomChargeIsProvidedAndTheSameExistsAsCurrentChargeWithNoModalThenItShouldKeepCustomChargeValue() {
        whenever(paymentConfiguration.charges).thenReturn(
            arrayListOf(
                PaymentTypeChargeRule(
                    "account_money",
                    BigDecimal.ONE
                )
            )
        )
        val customChargesMap: Map<String, CustomChargeDM> = mapOf(
            getAMCustomCharge()
        )
        val result = mapper.map(customChargesMap)
        val expectedResult = listOf(
            PaymentTypeChargeRule("account_money", BigDecimal.TEN, null, "Impuesto")
        )
        assertTrue(ReflectionEquals(result.first()).matches(expectedResult.first()))
    }

    @Test
    fun whenOneCustomChargeIsProvidedAndTheSameExistsAsCurrentChargeWithModalThenItShouldKeepModal() {
        whenever(paymentConfiguration.charges).thenReturn(
            arrayListOf(
                PaymentTypeChargeRule(
                    "account_money",
                    BigDecimal.ONE,
                    getDynamicDialog()
                )
            )
        )
        val customChargesMap: Map<String, CustomChargeDM> = mapOf(
            getAMCustomCharge()
        )
        val result = mapper.map(customChargesMap)
        val expectedResult = listOf(PaymentTypeChargeRule("account_money", BigDecimal.TEN, getDynamicDialog()))
        assertTrue(result.first().detailModal != null)
        assertTrue(ReflectionEquals(result.first().detailModal).matches(expectedResult.first().detailModal))
    }

    @Test
    fun whenCustomChargeFreeIsProvidedAndTheSameExistsAsCurrentChargeWithMessageThenItShouldKeepMessage() {
        whenever(paymentConfiguration.charges).thenReturn(
            arrayListOf(
                PaymentTypeChargeRule.createChargeFreeRule("account_money", "Sin comision")
            )
        )
        val customChargesMap: Map<String, CustomChargeDM> = mapOf(
            getAMCustomChargeFree()
        )
        val result = mapper.map(customChargesMap)
        val expectedResult = listOf(
            PaymentTypeChargeRule.createChargeFreeRule("account_money", "Sin comision")
        )
        assertTrue(ReflectionEquals(result.first()).matches(expectedResult.first()))
    }

    @Test
    fun whenCustomChargeIsProvidedAndTheSameExistsAsCurrentChargeFreeThenItShouldNotKeepMessageAndOverrideChargeValue() {
        whenever(paymentConfiguration.charges).thenReturn(
            arrayListOf(
                PaymentTypeChargeRule.createChargeFreeRule("account_money", "Sin comision")
            )
        )
        val customChargesMap: Map<String, CustomChargeDM> = mapOf(
            getAMCustomCharge()
        )
        val result = mapper.map(customChargesMap)
        val expectedResult = listOf(
            PaymentTypeChargeRule("account_money", BigDecimal.TEN, null, "Impuesto")
        )
        assertTrue(ReflectionEquals(result.first()).matches(expectedResult.first()))
    }

    private fun getAMCustomChargeFree() = Pair("account_money", CustomChargeDM(BigDecimal.ZERO, null))
    private fun getAMCustomCharge() = Pair("account_money", CustomChargeDM(BigDecimal.TEN, "Impuesto"))

    private fun getDynamicDialog() = object : DynamicDialogCreator {
        override fun shouldShowDialog(context: Context, checkoutData: DynamicDialogCreator.CheckoutData) = true
        override fun create(context: Context, checkoutData: DynamicDialogCreator.CheckoutData) = DialogFragment()
        override fun describeContents() = 0
        override fun writeToParcel(parcel: Parcel, i: Int) = Unit
    }
}