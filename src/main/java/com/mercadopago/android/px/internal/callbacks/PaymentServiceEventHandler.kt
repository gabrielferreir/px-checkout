package com.mercadopago.android.px.internal.callbacks

import com.mercadopago.android.px.internal.livedata.MutableSingleLiveData
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

internal class PaymentServiceEventHandler {
    val paymentFinishedLiveData = MutableSingleLiveData<PaymentModel>()
    val postPaymentStartedLiveData = MutableSingleLiveData<IPaymentDescriptor>()
    val recoverInvalidEscLiveData = MutableSingleLiveData<PaymentRecovery>()
    val paymentErrorLiveData = MutableSingleLiveData<MercadoPagoError>()
    val visualPaymentLiveData = MutableSingleLiveData<Unit>()
}