package com.mercadopago.android.px.internal.factory

import android.content.Context
import android.content.SharedPreferences
import com.mercadopago.android.px.internal.datasource.TransactionInfoFactory
import com.mercadopago.android.px.internal.di.FactoryModule
import com.mercadopago.android.px.internal.di.Session
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkClass
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class FactoryModuleTest {

    @MockK
    private lateinit var factoryModule: FactoryModule

    @Before
    fun `set up`() {
        MockKAnnotations.init(this)
        Session.initialize(getContext())
    }

    @Test
    fun `when transaction factory is called from factory module then return transaction info factory`() {
        val transactionInfoFactory = mockk<TransactionInfoFactory>()

        every { factoryModule.transactionInfoFactory } returns transactionInfoFactory

        assertNotNull(factoryModule.transactionInfoFactory)
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
