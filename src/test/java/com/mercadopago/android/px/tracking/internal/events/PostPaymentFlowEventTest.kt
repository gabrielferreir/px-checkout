package com.mercadopago.android.px.tracking.internal.events

import org.junit.Assert
import org.junit.Test

class PostPaymentFlowEventTest {

    @Test
    fun `When post payment flow event then path and destination are set accordingly`() {
        val expectedDeeplink = "mercadopago://integrator"

        val postPaymentFlowEvent = PostPaymentFlowEvent(expectedDeeplink)

        Assert.assertEquals(EXPECTED_PATH, postPaymentFlowEvent.getTrack().path)
        Assert.assertEquals(expectedDeeplink, postPaymentFlowEvent.getTrack().data[DESTINATION_KEY])
    }

    companion object {
        private const val EXPECTED_PATH = "/px_checkout/post_payment_flow"
        private const val DESTINATION_KEY = "destination"
    }
}
