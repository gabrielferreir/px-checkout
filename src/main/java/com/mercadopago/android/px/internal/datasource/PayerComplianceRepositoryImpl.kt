package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.PayerComplianceRepository
import com.mercadopago.android.px.model.PayerCompliance
import java.io.File

private const val PAYER_COMPLIANCE = "payer_compliance_repository"

internal class PayerComplianceRepositoryImpl(private val fileManager: FileManager) :
    AbstractLocalRepository<PayerCompliance?>(fileManager), PayerComplianceRepository {

    override val file: File = fileManager.create(PAYER_COMPLIANCE)

    override fun readFromStorage(): PayerCompliance? = fileManager.readParcelable(file, PayerCompliance.CREATOR)
}
