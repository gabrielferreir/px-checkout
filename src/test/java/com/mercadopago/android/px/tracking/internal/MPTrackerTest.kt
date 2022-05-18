package com.mercadopago.android.px.tracking.internal

import com.mercadopago.android.px.addons.model.Track
import com.mercadopago.android.px.addons.model.internal.Experiment
import com.mercadopago.android.px.addons.model.internal.Variant
import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.model.CheckoutType
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

private const val FLOW = "/test_flow"
private const val SESSION_ID = "/my_session_id"

@RunWith(MockitoJUnitRunner::class)
class MPTrackerTest {

    @Mock
    private lateinit var track: Track

    @Mock
    private lateinit var trackWrapper: TrackWrapper

    @Mock
    private lateinit var paymentConfiguration: PaymentConfiguration

    @Mock
    private lateinit var trackingRepository: TrackingRepository

    private val flowDetail = mapOf(Pair("flow", "detail"))

    private lateinit var tracker: MPTracker

    @Before
    fun setUp() {
        tracker = MPTracker(trackingRepository, paymentConfiguration)

        whenever(track.data).thenReturn(mutableMapOf())
        whenever(track.path).thenReturn("/track_path")
        whenever(track.type).thenReturn(Track.Type.EVENT)
        whenever(trackWrapper.getTrack()).thenReturn(track)
        whenever(trackWrapper.shouldTrackExperimentsLabel).thenReturn(true)

        whenever(trackingRepository.flowId).thenReturn(FLOW)
        whenever(trackingRepository.flowDetail).thenReturn(flowDetail)
        whenever(trackingRepository.sessionId).thenReturn(SESSION_ID)
        whenever(paymentConfiguration.hasPaymentProcessor()).thenReturn(true)
    }

    @Test
    fun whenTrackThenAddFlowSessionAndExtraInfo() {
        tracker.track(trackWrapper)

        assertEquals(FLOW, track.data["flow"])
        assertEquals(flowDetail, track.data["flow_detail"])
        assertEquals(SESSION_ID, track.data["session_id"])
        assertTrue(track.data["session_time"] as Long >= 0)
        assertEquals(CheckoutType.ONE_TAP, track.data["checkout_type"])
    }

    @Test
    fun whenTrackFrictionThenAddFlowSessionAndExtraInfo() {
        whenever(track.path).thenReturn(FrictionEventTracker.PATH)
        whenever(track.data).thenReturn(mapOf(Pair("extra_info", mutableMapOf<String, Any?>())))

        tracker.track(trackWrapper)

        val extraInfo = track.data["extra_info"] as Map<*, *>
        assertEquals(FLOW, extraInfo["flow"])
        assertNull(extraInfo["flow_detail"])
        assertEquals(SESSION_ID, extraInfo["session_id"])
        assertTrue(extraInfo["session_time"] as Long >= 0)
        assertEquals(CheckoutType.ONE_TAP, extraInfo["checkout_type"])
    }

    @Test
    fun whenTrackWithSecurityEnabledThenAddSecurityEnabledData() {
        whenever(trackingRepository.securityEnabled).thenReturn(true)
        tracker.track(trackWrapper)

        assertTrue(track.data["security_enabled"] as Boolean)
    }

    @Test
    fun whenTrackWithExperimentsThenAddExperimentsLabel() {
        val experiment1 = mock<Experiment>()
        val experiment2 = mock<Experiment>()
        val variant = mock<Variant>()
        whenever(variant.name).thenReturn("Variant")
        whenever(experiment1.name).thenReturn("Experiment1")
        whenever(experiment2.name).thenReturn("Experiment2")
        whenever(experiment1.variant).thenReturn(variant)
        whenever(experiment2.variant).thenReturn(variant)

        tracker.setExperiments(listOf(experiment1, experiment2))
        tracker.track(trackWrapper)

        assertEquals("Experiment1 - Variant,Experiment2 - Variant", track.data["experiments"])
    }

    @Test
    fun `when experiments label flag is false then data shouldn't include it`() {
        whenever(trackWrapper.shouldTrackExperimentsLabel).thenReturn(false)

        tracker.track(trackWrapper)

        assertFalse(track.data.containsKey("experiments"))
    }
}
