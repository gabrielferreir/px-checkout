package com.mercadopago.android.px.internal.features.payment_result.mappers

import com.mercadopago.android.px.internal.features.payment_result.CongratsAutoReturn
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.Payment
import com.mercadopago.android.px.model.internal.CongratsResponse

internal class CongratsAutoReturnMapper(private val autoReturnFromPreference: String?, private val paymentStatus: String)
    : Mapper<CongratsResponse.AutoReturn?, CongratsAutoReturn.Model?>() {

    override fun map(autoReturn: CongratsResponse.AutoReturn?): CongratsAutoReturn.Model? {
        return autoReturn?.let {
            CongratsAutoReturn.Model(it.label, it.seconds)
        } ?: CongratsAutoReturn.Model().takeIf {
            paymentStatus == Payment.StatusCodes.STATUS_APPROVED && CongratsAutoReturn.isValid(autoReturnFromPreference)
        }
    }
}
