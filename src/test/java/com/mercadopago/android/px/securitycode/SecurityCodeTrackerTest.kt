package com.mercadopago.android.px.securitycode

import com.mercadopago.android.px.internal.features.security_code.tracking.*
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SecurityCodeTrackerTest {

    private lateinit var securityCodeTracker: SecurityCodeTracker

    @Mock
    private lateinit var securityCodeViewTrackMock: SecurityCodeViewTrack

    @Mock
    private lateinit var confirmSecurityCodeTrackMock: ConfirmSecurityCodeTrack

    @Mock
    private lateinit var abortSecurityCodeTrackMock: AbortSecurityCodeTrack

    @Mock
    private lateinit var securityCodeFrictionsMock: SecurityCodeFrictions

    @Mock
    private lateinit var tracker: MPTracker

    @Before
    fun setUp() {
        securityCodeTracker = SecurityCodeTracker(
            tracker,
            securityCodeViewTrackMock,
            confirmSecurityCodeTrackMock,
            abortSecurityCodeTrackMock,
            securityCodeFrictionsMock)
    }

    @Test
    fun whenSecurityCodeViewTrack() {
        securityCodeTracker.trackSecurityCode()

        verify(tracker).track(securityCodeViewTrackMock)
    }

    @Test
    fun whenConfirmSecurityCodeTrack() {
        securityCodeTracker.trackConfirmSecurityCode()

        verify(tracker).track(confirmSecurityCodeTrackMock)
    }

    @Test
    fun whenAbortSecurityCodeTrack() {
        securityCodeTracker.trackAbortSecurityCode()

        verify(tracker).track(abortSecurityCodeTrackMock)
    }

    @Test
    fun whenSecurityCodeFrictions() {
        val apiErrorFrictionMock = mock(FrictionEventTracker::class.java)
        val tokenFrictionMock = mock(FrictionEventTracker::class.java)

        `when`(securityCodeFrictionsMock.paymentApiErrorFriction()).thenReturn(apiErrorFrictionMock)
        `when`(securityCodeFrictionsMock.tokenApiErrorFriction()).thenReturn(tokenFrictionMock)
        securityCodeTracker.trackPaymentApiError()
        securityCodeTracker.trackTokenApiError()

        verify(securityCodeFrictionsMock).paymentApiErrorFriction()
        verify(securityCodeFrictionsMock).tokenApiErrorFriction()
        verify(tracker).track(apiErrorFrictionMock)
        verify(tracker).track(tokenFrictionMock)
    }
}
