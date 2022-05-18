package com.mercadopago.android.px.internal.features.one_tap.confirm_button

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.features.pay_button.UIError
import com.mercadopago.android.px.internal.features.pay_button.UIProgress
import com.mercadopago.android.px.internal.util.nonNullObserve
import com.mercadopago.android.px.internal.util.nonNullObserveOnce

const val EXTRA_PAYMENT_METHODS_DATA = "EXTRA_PAYMENT_METHODS_DATA"
const val PAYMENT_METHODS_RESULT_CODE = 8

internal class SelectorConfirmButtonFragment : ConfirmButtonFragment<
    SelectorConfirmButtonViewModel.State,
    ConfirmButton.Handler>() {

    override val viewModel by viewModel<SelectorConfirmButtonViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            uiStateLiveData.nonNullObserve(viewLifecycleOwner) {
                when (it) {
                    is UIProgress.ButtonLoadingStarted -> startLoadingButton(it.timeOut, it.buttonConfig)
                    is UIProgress.ButtonLoadingCanceled -> cancelLoading()
                    is UIProgress.ButtonLoadingFinished -> finishLoading()
                    is UIError -> resolveError(it)
                    else -> Unit
                }
            }
            processFinished.nonNullObserveOnce(viewLifecycleOwner) { paymentMethodData ->
                activity?.apply {
                    val intent = Intent().also { it.putExtra(EXTRA_PAYMENT_METHODS_DATA, paymentMethodData) }
                    setResult(PAYMENT_METHODS_RESULT_CODE, intent)
                    finish()
                }
            }
        }
    }

    override fun onAnimationFinished() {
        viewModel.onAnimationFinished()
    }
}
