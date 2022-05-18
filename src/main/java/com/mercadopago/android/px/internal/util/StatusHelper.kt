package com.mercadopago.android.px.internal.util

import com.mercadopago.android.px.model.BusinessPayment
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.Payment

object StatusHelper {

    @JvmStatic
    fun isRejected(payment: IPaymentDescriptor): Boolean {
        return when(payment) {
            is BusinessPayment -> payment.decorator == BusinessPayment.Decorator.REJECTED
            else -> payment.paymentStatus == Payment.StatusCodes.STATUS_REJECTED
        }
    }

    @JvmStatic
    fun isSuccess(payment: IPaymentDescriptor): Boolean {
        return when(payment) {
            is BusinessPayment -> payment.decorator == BusinessPayment.Decorator.APPROVED
            else -> isGenericSuccess(payment)
        }
    }

    @JvmStatic
    fun isOfflineMethod(payment: IPaymentDescriptor) = payment.let {
        it.paymentStatus == Payment.StatusCodes.STATUS_PENDING && isPendingStatusDetailSuccess(it.paymentStatusDetail)
    }

    @JvmStatic
    fun isPendingStatusDetailSuccess(statusDetail: String): Boolean {
        return statusDetail == Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT ||
            statusDetail == Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_TRANSFER
    }

    private fun isGenericSuccess(payment: IPaymentDescriptor) = payment.let {
        it.paymentStatus == Payment.StatusCodes.STATUS_APPROVED || isOfflineMethod(it)
    }
}
