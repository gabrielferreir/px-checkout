package com.mercadopago.android.px.internal.helpers

import android.content.Context
import android.content.SharedPreferences
import com.mercadopago.android.px.internal.di.HelperModule
import com.mercadopago.android.px.internal.di.Session
import com.mercadopago.android.px.internal.features.payment_result.model.DisplayInfoHelper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkClass
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class HelperModuleTest  {

    @MockK
    private lateinit var helperModule: HelperModule

    @Before
    fun `set up`() {
        MockKAnnotations.init(this)
        Session.initialize(getContext())
    }

    @Test
    fun `when display info helper is called from factory module then return display info helper`() {
        val displayInfoHelper = mockk<DisplayInfoHelper>()

        every { helperModule.displayInfoHelper } returns displayInfoHelper

        assertNotNull(helperModule.displayInfoHelper)
    }

    private fun getContext(): Context {
        val sharedPreferencesMock = mockkClass(SharedPreferences::class)
        val applicationContextMock = mockkClass(Context::class) {
            every { getSharedPreferences(any(), any()) } returns sharedPreferencesMock
        }
        return mockkClass(Context::class) {
            every { applicationContext } returns applicationContextMock
        }
    }
}
