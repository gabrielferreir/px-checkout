package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.internal.OneTapItem

internal class CustomOptionIdSolverImpl(private val applicationSelectionRepository: ApplicationSelectionRepository)
    : CustomOptionIdSolver() {

    override fun get(oneTapItem: OneTapItem): String {
        val selectedPaymentMethod = applicationSelectionRepository[oneTapItem].paymentMethod

        return when {
            PaymentTypes.isCardPaymentType(selectedPaymentMethod.type) -> oneTapItem.card.id
            oneTapItem.isOfflineMethods -> oneTapItem.getDefaultPaymentMethodType()
            oneTapItem.isBankTransfer() -> oneTapItem.bankTransfer!!.id
            else -> selectedPaymentMethod.id
        }
    }
}