package com.mercadopago.android.px.securitycode

import com.mercadopago.android.px.addons.model.Track
import com.mercadopago.android.px.internal.features.security_code.tracking.*
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class SecurityTrackTest {
    @Mock
    private lateinit var securityCodeData: TrackingMapModel

    @Before
    fun setUp() {
        `when`(securityCodeData.toMap()).thenReturn(mapOf(
            "payment_method_id" to "visa",
            "payment_method_type" to "credit_card",
            "card_id" to "123",
            "issuer_id" to 1234,
            "bin" to "123456"
        ))
    }

    @Test
    fun whenSecurityCodeViewTrack() {
        val securityCodeViewTrack = SecurityCodeViewTrack(securityCodeData, Reason.NO_REASON)
        val resultActual = securityCodeViewTrack.getTrack()
        val resultExpectedMap = securityCodeData.toMap().also { it["reason"] = Reason.NO_REASON.name.toLowerCase(Locale.getDefault()) }
        val resultExpected = TrackFactory.withView(SecurityCodeTrack.ACTION_BASE_PATH).addData(resultExpectedMap).build()

        assertEqualsTracks(resultActual, resultExpected)
    }

    @Test
    fun whenAbortSecurityCodeTrack() {
        val abortSecurityCodeTrack = AbortSecurityCodeTrack(securityCodeData, Reason.NO_REASON)
        val resultActual = abortSecurityCodeTrack.getTrack()
        val resultExpectedMap = securityCodeData.toMap().also { it["reason"] = Reason.NO_REASON.name.toLowerCase(Locale.getDefault()) }
        val resultExpected = TrackFactory.withView("${SecurityCodeTrack.ACTION_BASE_PATH}/abort").addData(resultExpectedMap).build()

        assertEqualsTracks(resultActual, resultExpected)
    }

    @Test
    fun whenConfirmSecurityCodeTrack() {
        val confirmSecurityCodeTrack = ConfirmSecurityCodeTrack(securityCodeData, Reason.NO_REASON)
        val resultActual = confirmSecurityCodeTrack.getTrack()
        val resultExpectedMap = securityCodeData.toMap().also { it["reason"] = Reason.NO_REASON.name.toLowerCase(Locale.getDefault()) }
        val resultExpected = TrackFactory.withView("${SecurityCodeTrack.ACTION_BASE_PATH}/confirm").addData(resultExpectedMap).build()

        assertEqualsTracks(resultActual, resultExpected)
    }

    @Test
    fun whenPaymentApiErrorFriction() {
        val paymentApiErrorFrictionTrack = SecurityCodeFrictions(securityCodeData).paymentApiErrorFriction()
        val resultActual = paymentApiErrorFrictionTrack.getTrack()
        val resultExpectedMap = securityCodeData.toMap()
        val frictionId = FrictionEventTracker.Id.PAYMENTS_API_ERROR

        val resultExpected = FrictionEventTracker.with(
            "${SecurityCodeTrack.ACTION_BASE_PATH}/${frictionId.name.toLowerCase(Locale.getDefault())}",
            frictionId,
            FrictionEventTracker.Style.SNACKBAR,
            resultExpectedMap).getTrack()

        assertEqualsTracks(resultActual, resultExpected)
    }

    @Test
    fun whenTokenApiErrorFriction() {
        val tokenApiErrorFrictionTrack = SecurityCodeFrictions(securityCodeData).tokenApiErrorFriction()
        val resultActual = tokenApiErrorFrictionTrack.getTrack()
        val resultExpectedMap = securityCodeData.toMap()
        val frictionId = FrictionEventTracker.Id.TOKEN_API_ERROR

        val resultExpected = FrictionEventTracker.with(
            "${SecurityCodeTrack.ACTION_BASE_PATH}/${frictionId.name.toLowerCase(Locale.getDefault())}",
            frictionId,
            FrictionEventTracker.Style.SNACKBAR,
            resultExpectedMap).getTrack()

        assertEqualsTracks(resultActual, resultExpected)
    }

    private fun assertEqualsTracks(actual: Track, expected: Track) {
        assertEquals(actual.data, expected.data)
        assertEquals(actual.path, expected.path)
        assertEquals(actual.experiments, expected.experiments)
        assertEquals(actual.type, expected.type)
        assertEquals(actual.applicationContext, expected.applicationContext)
    }
}
