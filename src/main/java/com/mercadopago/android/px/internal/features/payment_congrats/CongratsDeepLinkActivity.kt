package com.mercadopago.android.px.internal.features.payment_congrats

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mercadolibre.android.ui.widgets.MeliSpinner
import com.mercadopago.android.px.R
import com.mercadopago.android.px.configuration.PostPaymentConfiguration.Companion.EXTRA_BUNDLE
import com.mercadopago.android.px.configuration.PostPaymentConfiguration.Companion.EXTRA_PAYMENT
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.features.Constants.RESULT_CUSTOM_EXIT
import com.mercadopago.android.px.internal.features.dummy_result.DummyResultActivity
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultActivity
import com.mercadopago.android.px.internal.util.ErrorUtil
import com.mercadopago.android.px.internal.util.MercadoPagoUtil
import com.mercadopago.android.px.internal.util.nonNullObserve
import com.mercadopago.android.px.model.ExitAction
import com.mercadopago.android.px.model.IParcelablePaymentDescriptor
import com.mercadopago.android.px.model.Payment
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

private const val REQ_CODE_CONGRATS = 300
private const val REQ_CODE_SKIP_CONGRATS = 301

internal class CongratsDeepLinkActivity : AppCompatActivity() {

    private val congratsViewModel by viewModel<CongratsViewModel>()

    private var iPaymentDescriptor: IParcelablePaymentDescriptor? = null
    private lateinit var customDataBundle: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congrats_deep_link)

        val bundle = intent.getBundleExtra(EXTRA_BUNDLE)
        iPaymentDescriptor = bundle?.getParcelable(EXTRA_PAYMENT) as? IParcelablePaymentDescriptor
        congratsViewModel.createCongratsResult(iPaymentDescriptor)

        congratsViewModel.congratsResultLiveData.nonNullObserve(this) { onCongratsResult(it) }
        congratsViewModel.postPaymentUrlsLiveData.nonNullObserve(this) { onCongratsPostPaymentUrl(it) }
        congratsViewModel.exitFlowLiveData.nonNullObserve(this) { onCongratsPostPaymentUrl(it) }
    }

    override fun onBackPressed() {
        if (congratsViewModel.congratsResultLiveData.value != CongratsPostPaymentResult.Loading) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            requestCode == ErrorUtil.ERROR_REQUEST_CODE && resultCode == RESULT_OK ->
                congratsViewModel.createCongratsResult(iPaymentDescriptor)
            resultCode == RESULT_CUSTOM_EXIT -> handleCustomExit(data)
            else -> {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun onCongratsResult(congratsResult: CongratsResult) {
        findViewById<MeliSpinner>(R.id.loading_view).visibility = View.GONE
        when (congratsResult) {
            is CongratsResult.PaymentResult -> {
                PaymentResultActivity.start(this, REQ_CODE_CONGRATS, congratsResult.paymentModel)
            }
            is CongratsResult.BusinessPaymentResult -> {
                PaymentCongrats.showWithSession(congratsResult.paymentCongratsModel, this, REQ_CODE_CONGRATS)
            }
            is CongratsPaymentResult.SkipCongratsResult -> {
                DummyResultActivity.start(
                    this,
                    REQ_CODE_SKIP_CONGRATS,
                    congratsResult.paymentModel
                )
            }
            is CongratsPostPaymentResult.Loading ->
                findViewById<MeliSpinner>(R.id.loading_view).visibility = View.VISIBLE
            is CongratsPostPaymentResult.ConnectionError -> handleError(
                message = getString(R.string.px_no_connection_message),
                recoverable = true
            )
            is CongratsPostPaymentResult.BusinessError -> handleError(
                recoverable = false,
                message = congratsResult.message.orEmpty()
            )
        }
    }

    private fun onCongratsPostPaymentUrl(congratsPostPaymentUrlsResponse: CongratsPostPaymentUrlsResponse?) {
        when (congratsPostPaymentUrlsResponse) {
            is CongratsPostPaymentUrlsResponse.OnGoToLink -> navigateToBackUrl(congratsPostPaymentUrlsResponse.link)
            is CongratsPostPaymentUrlsResponse.OnOpenInWebView ->
                openInWebView(congratsPostPaymentUrlsResponse.link)
            is CongratsPostPaymentUrlsResponse.OnExitWith -> finishWithPaymentResult(
                congratsPostPaymentUrlsResponse.customResponseCode,
                congratsPostPaymentUrlsResponse.payment
            )
        }
    }

    private fun handleError(message: String, recoverable: Boolean) {
        ErrorUtil.startErrorActivity(this, MercadoPagoError(message, recoverable))
    }

    private fun handleCustomExit(data: Intent?) {
        if (data != null) {
            when {
                data.hasExtra(ExitAction.EXTRA_CLIENT_RES_CODE) -> {
                    //Business custom exit
                    val resCode = data.getIntExtra(ExitAction.EXTRA_CLIENT_RES_CODE, RESULT_OK)
                    congratsViewModel.onPaymentResultResponse(resCode)
                }
                data.hasExtra(PaymentResultActivity.EXTRA_RESULT_CODE) -> {
                    //Custom exit  - Result screen.
                    val finalResultCode = data.getIntExtra(
                        PaymentResultActivity.EXTRA_RESULT_CODE,
                        MercadoPagoCheckout.PAYMENT_RESULT_CODE
                    )
                    customDataBundle = data
                    congratsViewModel.onPaymentResultResponse(finalResultCode)
                }
                else -> {
                    //Normal exit - Result screen.
                    congratsViewModel.onPaymentResultResponse(null)
                }
            }
        } else {
            //Normal exit - Result screen.
            congratsViewModel.onPaymentResultResponse(null)
        }
    }

    private fun navigateToBackUrl(link: String) {
        runCatching {
            val intent = MercadoPagoUtil.getIntent(link)
            startActivity(intent)
        }.onFailure { exception ->
            exception.printStackTrace()
        }
    }

    private fun openInWebView(link: String) {
        runCatching {
            val intent = MercadoPagoUtil.getNativeOrWebViewIntent(this, link)
            startActivity(intent)
        }.onFailure { exception ->
            exception.printStackTrace()
        }
    }

    private fun finishWithPaymentResult(resultCode: Int?, payment: Payment?) {
        var defaultResultCode = RESULT_OK
        val intent = Intent()

        if (this::customDataBundle.isInitialized) {
            intent.putExtras(customDataBundle)
        }

        if (payment != null) {
            defaultResultCode = MercadoPagoCheckout.PAYMENT_RESULT_CODE
            intent.putExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT, payment)
        }

        setResult(resultCode ?: defaultResultCode, intent)
        finish()
    }

}
