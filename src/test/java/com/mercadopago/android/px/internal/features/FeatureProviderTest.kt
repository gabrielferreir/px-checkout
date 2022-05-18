package com.mercadopago.android.px.internal.features

import com.mercadopago.android.px.addons.TokenDeviceBehaviour
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.core.internal.ConfigurationProvider
import com.mercadopago.android.px.core.v2.PaymentProcessor
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.CheckoutFeatures
import com.mercadopago.android.px.preferences.CheckoutPreference
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
internal class FeatureProviderTest {

    @Mock
    private lateinit var configurationProvider: ConfigurationProvider

    @Mock
    private lateinit var tokenDeviceBehaviour: TokenDeviceBehaviour

    @Mock
    lateinit var paymentConfiguration: PaymentConfiguration

    @Mock
    lateinit var checkoutPreference: CheckoutPreference

    @Mock
    lateinit var mercadoPagoCheckout: MercadoPagoCheckout

    @Before
    fun setUp() {
        whenever(configurationProvider.paymentConfiguration).thenReturn(paymentConfiguration)
        whenever(configurationProvider.checkoutPreference).thenReturn(checkoutPreference)
        whenever(mercadoPagoCheckout.paymentConfiguration).thenReturn(paymentConfiguration)
        whenever(mercadoPagoCheckout.checkoutPreference).thenReturn(checkoutPreference)
    }

    @Test
    fun testBasicFeatures() {
        val checkoutFeatures = FeatureProviderImpl(configurationProvider, tokenDeviceBehaviour).availableFeatures
        Assert.assertTrue(checkoutFeatures.express)
        Assert.assertTrue(checkoutFeatures.comboCard)
        Assert.assertTrue(checkoutFeatures.hybridCard)
        Assert.assertTrue(checkoutFeatures.odrFlag)
        Assert.assertTrue(checkoutFeatures.customTaxesCharges)
        checkoutFeatures.styleVersion.assertEquals(CheckoutFeatures.STYLE_VERSION)
        Assert.assertTrue(checkoutFeatures.cardsCustomTaxesCharges)
        Assert.assertTrue(checkoutFeatures.taxableCharges)
    }

    @Test
    fun testWhenPaymentProcessorDoesNotSupportSplit_thenNoSplitFeature() {
        whenever(paymentConfiguration.getSupportsSplit(checkoutPreference)).thenReturn(false)
        val checkoutFeatures = FeatureProviderImpl(configurationProvider, tokenDeviceBehaviour).availableFeatures
        Assert.assertFalse(checkoutFeatures.split)
    }

    @Test
    fun testWhenPaymentProcessorSupportSplit_thenSplitFeature() {
        whenever(paymentConfiguration.getSupportsSplit(checkoutPreference)).thenReturn(true)
        val checkoutFeatures = FeatureProviderImpl(configurationProvider, tokenDeviceBehaviour).availableFeatures
        Assert.assertTrue(checkoutFeatures.split)
    }

    @Test
    fun testWhenCheckoutIsLazyInitAndPaymentProcessorSupportSplit_thenSplitFeature() {
        whenever(paymentConfiguration.getSupportsSplit(checkoutPreference)).thenReturn(true)
        val checkoutFeatures = FeatureProviderImpl(mercadoPagoCheckout, tokenDeviceBehaviour).availableFeatures
        Assert.assertTrue(checkoutFeatures.split)
    }

    @Test
    fun testWhenTokenDeviceFeatureIsAvailable_thenValidationProgramsFeatureContainsTokenDevice() {
        whenever(tokenDeviceBehaviour.isFeatureAvailable).thenReturn(true)
        val checkoutFeatures = FeatureProviderImpl(configurationProvider, tokenDeviceBehaviour).availableFeatures
        Assert.assertTrue(checkoutFeatures.validationPrograms.contains(Application.KnownValidationProgram.TOKEN_DEVICE.value))
    }

    @Test
    fun testWhenTokenDeviceFeatureIsNotAvailable_thenValidationProgramsFeatureDoesNotContainsTokenDevice() {
        whenever(tokenDeviceBehaviour.isFeatureAvailable).thenReturn(false)
        val checkoutFeatures = FeatureProviderImpl(configurationProvider, tokenDeviceBehaviour).availableFeatures
        Assert.assertFalse(checkoutFeatures.validationPrograms.contains(Application.KnownValidationProgram.TOKEN_DEVICE.value))
    }

}