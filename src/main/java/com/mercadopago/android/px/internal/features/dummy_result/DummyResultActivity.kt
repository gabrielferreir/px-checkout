package com.mercadopago.android.px.internal.features.dummy_result

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.internal.base.MvpView
import com.mercadopago.android.px.internal.base.PXActivity
import com.mercadopago.android.px.internal.di.Session
import com.mercadopago.android.px.internal.features.Constants
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultActivity
import com.mercadopago.android.px.internal.util.MercadoPagoUtil
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel

internal class DummyResultActivity : PXActivity<DummyResultPresenter>(), MvpView {

    override fun onCreated(savedInstanceState: Bundle?) {
        val paymentModel = intent.getParcelableExtra<PaymentModel>(PaymentResultActivity.EXTRA_PAYMENT_MODEL) ?:
            error("PaymentModel can't be null")
        val session = Session.getInstance()
        val configuration = session.configurationModule
        val presenter = DummyResultPresenter(
            paymentModel,
            configuration.paymentSettings.advancedConfiguration.paymentResultScreenConfiguration,
            configuration.paymentSettings, MercadoPagoUtil.isMP(this),
            session.helperModule.bankInfoHelper,
            session.tracker
        )
        presenter.attachView(this)
        Intent().let {
            it.putExtra(PaymentResultActivity.EXTRA_RESULT_CODE,
                if (paymentModel is BusinessPaymentModel) Activity.RESULT_OK else MercadoPagoCheckout.PAYMENT_RESULT_CODE)
            setResult(Constants.RESULT_CUSTOM_EXIT, it)
            finish()
        }
    }

    companion object {
        fun start(fragment: Fragment, requestCode: Int, model: PaymentModel) {
            fragment.activity?.let {
                if (it is PXActivity<*>) it.overrideTransitionIn()
                val intent = Intent(it, DummyResultActivity::class.java)
                intent.putExtra(PaymentResultActivity.EXTRA_PAYMENT_MODEL, model)
                fragment.startActivityForResult(intent, requestCode)
            }
        }

        fun start(activity: Activity, requestCode: Int, model: PaymentModel) {
            if (activity is PXActivity<*>) activity.overrideTransitionIn()
            val intent = Intent(activity, DummyResultActivity::class.java)
            intent.putExtra(PaymentResultActivity.EXTRA_PAYMENT_MODEL, model)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}
