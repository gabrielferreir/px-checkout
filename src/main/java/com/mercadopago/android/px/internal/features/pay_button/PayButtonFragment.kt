package com.mercadopago.android.px.internal.features.pay_button

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.mercadopago.android.px.addons.BehaviourProvider
import com.mercadopago.android.px.addons.internal.SecurityValidationHandler
import com.mercadopago.android.px.addons.model.SecurityValidationData
import com.mercadopago.android.px.configuration.PostPaymentConfiguration.Companion.EXTRA_BUNDLE
import com.mercadopago.android.px.configuration.PostPaymentConfiguration.Companion.EXTRA_PAYMENT
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.features.Constants
import com.mercadopago.android.px.internal.features.dummy_result.DummyResultActivity
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButtonFragment
import com.mercadopago.android.px.internal.features.payment_congrats.CongratsPaymentResult
import com.mercadopago.android.px.internal.features.payment_congrats.CongratsResult
import com.mercadopago.android.px.internal.features.payment_congrats.PaymentCongrats
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultActivity
import com.mercadopago.android.px.internal.features.plugins.PaymentProcessorActivity
import com.mercadopago.android.px.internal.util.ErrorUtil
import com.mercadopago.android.px.internal.util.MercadoPagoUtil
import com.mercadopago.android.px.internal.util.nonNullObserve
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import com.mercadopago.android.px.tracking.internal.events.PostPaymentFlowEvent

private const val REQ_CODE_CONGRATS = 300
private const val REQ_CODE_PAYMENT_PROCESSOR = 302
private const val REQ_CODE_BIOMETRICS = 303
private const val EXTRA_OBSERVING = "extra_observing"

internal class PayButtonFragment :
    ConfirmButtonFragment<PayButtonViewModel.State, PayButton.Handler>(),
    PayButton.View,
    SecurityValidationHandler {

    override val viewModel by viewModel<PayButtonViewModel>()
    private var observingPostPaymentAction = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            observingPostPaymentAction = it.getBoolean(EXTRA_OBSERVING)
        }

        if (observingPostPaymentAction) {
            observePostPaymentAction()
        }

        with(viewModel) {
            uiStateLiveData.nonNullObserve(viewLifecycleOwner, ::onStateUIChanged)
            postPaymentLiveData.nonNullObserve(viewLifecycleOwner, ::onPostPaymentFlow)
            congratsResultLiveData.nonNullObserve(viewLifecycleOwner, ::onCongratsResult)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_OBSERVING, observingPostPaymentAction)
    }

    private fun onStateUIChanged(stateUI: ConfirmButtonUiState) {
        when (stateUI) {
            is UIProgress.FingerprintRequired -> startBiometricsValidation(stateUI.validationData)
            is UIProgress.ButtonLoadingStarted -> startLoadingButton(stateUI.timeOut, stateUI.buttonConfig)
            is UIProgress.ButtonLoadingFinished -> finishLoading(stateUI.explodeDecorator)
            is UIProgress.ButtonLoadingCanceled -> cancelLoading()
            is UIResult.VisualProcessorResult -> PaymentProcessorActivity.start(this, REQ_CODE_PAYMENT_PROCESSOR)
            is UIError -> resolveError(stateUI)
            else -> Unit
        }
    }

    private fun onPostPaymentFlow(postPaymentFlowStarted: PostPaymentFlowStarted) {
        with(postPaymentFlowStarted) {
            launchPostPaymentFlow(postPaymentDeepLinkUrl, iParcelablePaymentDescriptor)
        }
    }

    private fun onCongratsResult(congratsResult: CongratsResult) {
        when (congratsResult) {
            is CongratsResult.PaymentResult -> PaymentResultActivity.start(
                this,
                REQ_CODE_CONGRATS,
                congratsResult.paymentModel
            )
            is CongratsResult.BusinessPaymentResult -> PaymentCongrats.show(
                congratsResult.paymentCongratsModel,
                this,
                REQ_CODE_CONGRATS
            )
            is CongratsPaymentResult.SkipCongratsResult -> DummyResultActivity.start(
                this,
                REQ_CODE_CONGRATS,
                congratsResult.paymentModel
            )
        }
    }

    private fun launchPostPaymentFlow(deepLink: String, extraData: Parcelable?) {
        runCatching {
            val intent = MercadoPagoUtil.getIntent(deepLink)
            extraData?.let { data ->
                val bundle = Bundle()
                bundle.putParcelable(EXTRA_PAYMENT, data)
                intent.putExtra(EXTRA_BUNDLE, bundle)
            }
            startActivity(intent)
            activity?.finish()
        }.onSuccess {
            viewModel.track(PostPaymentFlowEvent(deepLink))
        }.onFailure { exception ->
            viewModel.track(
                FrictionEventTracker.with(
                    "${TrackWrapper.BASE_PATH}/post_payment_deep_link",
                    FrictionEventTracker.Id.INVALID_POST_PAYMENT_DEEP_LINK,
                    FrictionEventTracker.Style.SCREEN,
                    MercadoPagoError.createNotRecoverable(exception.message.orEmpty())
                )
            )
            ErrorUtil.startErrorActivity(this, MercadoPagoError("", false))
        }
    }

    override fun stimulate() {
        viewModel.preparePayment()
    }

    private fun startBiometricsValidation(validationData: SecurityValidationData) {
        disable()
        BehaviourProvider.getSecurityBehaviour().startValidation(this, validationData, REQ_CODE_BIOMETRICS)
    }

    override fun onResultIconAnimation() {
        viewModel.onResultIconAnimation()
    }

    override fun onAnimationFinished() {
        viewModel.onAnimationFinished()
    }

    override fun onSecurityValidated(isSuccess: Boolean, securityValidated: Boolean) {
        if (!isSuccess) {
            enable()
        }
        viewModel.handleAuthenticationResult(isSuccess, securityValidated)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_CODE_BIOMETRICS) {
            val securityRequested = data?.getBooleanExtra(
                BehaviourProvider.getSecurityBehaviour().extraResultKey, false
            ) ?: false
            onSecurityValidated(resultCode == Activity.RESULT_OK, securityRequested)
        } else if (requestCode == REQ_CODE_CONGRATS) {
            if (resultCode == Constants.RESULT_ACTION) {
                handleAction(data)
            } else {
                viewModel.handleCongratsResult(resultCode, data)
            }
        } else if (resultCode == Constants.RESULT_PAYMENT) {
            viewModel.onPostPayment(PaymentProcessorActivity.getPaymentModel(data))
        } else if (resultCode == Constants.RESULT_FAIL_ESC) {
            viewModel.onRecoverPaymentEscInvalid(PaymentProcessorActivity.getPaymentRecovery(data)!!)
        } else if (
            requestCode == ErrorUtil.ERROR_REQUEST_CODE ||
            requestCode == REQ_CODE_PAYMENT_PROCESSOR &&
            resultCode == RESULT_CANCELED
        ) {
            activity?.finish()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleAction(data: Intent?) {
        data?.extras?.let { viewModel.onPostPaymentAction(PostPaymentAction.fromBundle(it)) }
    }

    override fun shouldSkipRevealAnimation() = viewModel.skipRevealAnimation()

    fun observePostPaymentAction() {
        fragmentCommunicationViewModel?.apply {
            observingPostPaymentAction = true
            postPaymentActionLiveData.nonNullObserve(viewLifecycleOwner) {
                viewModel.onPostPaymentAction(it)
            }
        }
    }
}
