package com.mercadopago.android.px.internal.features.dummy_result

import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration
import com.mercadopago.android.px.internal.base.BasePresenter
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.tracking.internal.BankInfoHelper
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack

internal class DummyResultPresenter(private val paymentModel: PaymentModel,
    private val paymentResultScreenConfiguration: PaymentResultScreenConfiguration,
    private val paymentSettingRepository: PaymentSettingRepository,
    private val isMP: Boolean,
    private val bankInfoHelper: BankInfoHelper,
    tracker: MPTracker) : BasePresenter<DummyResultActivity>(tracker) {

    override fun attachView(view: DummyResultActivity) {
        super.attachView(view)
        if (paymentModel is PaymentCongratsModel) {
            track(ResultViewTrack(paymentModel, isMP, bankInfoHelper))
        } else {
            track(ResultViewTrack(paymentModel, paymentResultScreenConfiguration, paymentSettingRepository, isMP, bankInfoHelper))
        }
    }
}
