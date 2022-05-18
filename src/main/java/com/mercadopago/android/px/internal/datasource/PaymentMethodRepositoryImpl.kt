package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.PaymentMethodRepository
import com.mercadopago.android.px.model.PaymentMethod
import java.io.File

private const val PAYMENT_METHODS = "payment_methods_repository"

internal class PaymentMethodRepositoryImpl(private val fileManager: FileManager) :
    AbstractLocalRepository<List<PaymentMethod>>(fileManager), PaymentMethodRepository {

    override val file: File = fileManager.create(PAYMENT_METHODS)

    override fun readFromStorage() = fileManager.readAnyList(file, PaymentMethod::class.java)

    override fun getPaymentMethodById(paymentMethodId: String): PaymentMethod {
        return value.firstOrNull { it.id == paymentMethodId } ?: throw IllegalStateException("PaymentMethod not found")
    }

}