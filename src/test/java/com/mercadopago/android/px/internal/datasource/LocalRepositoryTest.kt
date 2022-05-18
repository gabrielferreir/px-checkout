package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.util.TextUtil
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
internal class LocalRepositoryTest {

    @Mock
    private lateinit var fileManager: FileManager

    @Mock
    private lateinit var file: File

    private val localRepository by lazy { StubLocalRepository(fileManager, file) }

    @Test
    fun testRepositoryValueWhenNotConfigured_thenReturn_empty() {
        `when`(fileManager.readText(file)).thenReturn(TextUtil.EMPTY)
        Assert.assertEquals(TextUtil.EMPTY, localRepository.value)
        verify(fileManager, times(1)).readText(file)
    }

    @Test
    fun testRepositoryValueWhenConfigured_thenReturn_configuredValue() {
        val mockValue = "MOCK_VALUE"
        localRepository.configure(mockValue)
        Assert.assertEquals(mockValue, localRepository.value)
        verify(fileManager, never()).readText(file)
    }

    @Test
    fun testRepositoryReset_then_fileManagerRemoveFileCalledOnce() {
        localRepository.reset()
        verify(fileManager, times(1)).removeFile(file)
    }


    private class StubLocalRepository(private val fileManager: FileManager, override val file: File) :
        AbstractLocalRepository<String>(fileManager) {
        override fun readFromStorage(): String {
            return fileManager.readText(file)
        }
    }
}