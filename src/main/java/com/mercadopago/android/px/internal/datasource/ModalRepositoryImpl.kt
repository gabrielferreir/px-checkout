package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.ModalRepository
import com.mercadopago.android.px.model.internal.Modal
import java.io.File

private const val MODAL = "modals_repository"

internal class ModalRepositoryImpl(private val fileManager: FileManager) :
    AbstractLocalRepository<Map<String, Modal>>(fileManager), ModalRepository {

    override val file: File = fileManager.create(MODAL)

    override fun readFromStorage(): Map<String, Modal> =
        fileManager.readAnyMap(file, String::class.java, Modal::class.java)
}