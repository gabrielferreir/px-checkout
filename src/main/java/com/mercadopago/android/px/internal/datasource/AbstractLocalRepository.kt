package com.mercadopago.android.px.internal.datasource

import androidx.annotation.CallSuper
import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.LocalRepository
import java.io.File

internal abstract class AbstractLocalRepository<T>(private val fileManager: FileManager) : LocalRepository<T> {
    abstract val file: File
    private var internalValue: T? = null
    override val value: T
        get() = internalValue ?: readFromStorage().also { internalValue = it }

    @CallSuper
    override fun configure(value: T) {
        internalValue = value
        value?.let { fileManager.writeToFile(file, it) }
    }

    abstract fun readFromStorage(): T

    @CallSuper
    override fun reset() {
        fileManager.removeFile(file)
    }
}