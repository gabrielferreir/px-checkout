package com.mercadopago.android.px.securitycode

import com.mercadopago.android.px.CallbackTest
import com.mercadopago.android.px.TestContextProvider
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.SecurityTrackModelUseCase
import com.mercadopago.android.px.internal.features.security_code.tracking.SecurityCodeTracker
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.model.Reason
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class SecurityTrackModelUseCaseTest {

    @Mock
    private lateinit var success: CallbackTest<SecurityCodeTracker>

    @Mock
    private lateinit var failure: CallbackTest<MercadoPagoError>
    private lateinit var securityTrackModelUseCase: SecurityTrackModelUseCase

    @Before
    fun setUp() {
        val contextProvider = TestContextProvider()

        securityTrackModelUseCase = SecurityTrackModelUseCase(mock(), contextProvider)
    }

    @Test
    fun whenGetTrackData() {
        val trackingParams = SecurityTrackModelUseCase.SecurityTrackModelParams(
            mock(),
            Reason.INVALID_ESC
        )
        securityTrackModelUseCase.execute(
            trackingParams,
            success::invoke,
            failure::invoke
        )

        verify(success).invoke(any())
        verifyNoInteractions(failure)
    }
}
