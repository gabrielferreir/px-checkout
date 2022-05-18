package com.mercadopago.android.px.internal.features.payment_result.remedies

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.extensions.visible
import com.mercadopago.android.px.internal.features.generic_modal.*
import com.mercadopago.android.px.internal.features.generic_modal.GenericDialog.Companion.showDialog
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton
import com.mercadopago.android.px.internal.features.pay_button.PayButton
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultButton
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultFooter
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.CvvRemedy
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.HighRiskRemedy
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.RetryPaymentFragment
import com.mercadopago.android.px.internal.util.MercadoPagoUtil
import com.mercadopago.android.px.internal.util.nonNullObserve
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.internal.viewmodel.TextLocalized

internal class RemediesFragment private constructor(): Fragment(), Remedies.View, CvvRemedy.Listener, PaymentResultFooter.Listener,
    GenericDialog.Listener {

    private val viewModel: RemediesViewModel by viewModel()
    private var listener: Listener? = null
    private lateinit var retryPaymentFragment: RetryPaymentFragment
    private lateinit var retryPaymentContainer: View
    private lateinit var highRisk: HighRiskRemedy


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_remedies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retryPaymentContainer = view.findViewById(R.id.retry_payment_container)
        highRisk = view.findViewById(R.id.high_risk)
        arguments?.apply {
            val paymentModel = getParcelable<PaymentModel>(PAYMENT_MODEL)
            val remediesModel = getParcelable<RemediesModel>(REMEDIES_MODEL)
            checkNotNull(paymentModel) { "PaymentModel should not be null"}
            checkNotNull(remediesModel) { "RemediesModel should not be null"}
            viewModel.init(remediesModel, paymentModel)
            retryPaymentFragment = childFragmentManager.findFragmentById(R.id.retry_payment) as RetryPaymentFragment
            retryPaymentFragment.setListener(this@RemediesFragment)
            buildViewModel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw IllegalStateException("Parent should implement remedies listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCvvFilled(cvv: String) {
        viewModel.onCvvFilled(cvv)
        listener?.enablePayButton()
    }

    override fun onCvvDeleted() {
        listener?.disablePayButton()
    }

    override fun onPrePayment(callback: ConfirmButton.OnReadyForProcessCallback) {
        viewModel.onPrePayment(callback)
    }

    override fun onPayButtonPressed(callback: ConfirmButton.OnEnqueueResolvedCallback) {
        viewModel.onPayButtonPressed(callback)
    }

    override fun onClick(action: PaymentResultButton.Action) {
        viewModel.onButtonPressed(action)
    }

    private fun goToKyc(deepLink: String) {
        activity?.let {
            startActivity(MercadoPagoUtil.getSafeIntent(it, Uri.parse(deepLink)))
        }
    }

    private fun buildViewModel() {
        viewModel.remedyState.nonNullObserve(viewLifecycleOwner) {
            when (it) {
                is RemedyState.ShowRetryPaymentRemedy -> {
                    retryPaymentContainer.visible()
                    val (model, methodData) = it.data
                    retryPaymentFragment.init(model, methodData)
                }

                is RemedyState.ShowKyCRemedy -> {
                    highRisk.visible()
                    highRisk.init(it.model)
                }

                is RemedyState.GoToKyc -> {
                    listener?.onUserValidation()
                    goToKyc(it.deepLink)
                }

                is RemedyState.ChangePaymentMethod -> {
                    listener?.changePaymentMethod()
                }

                is RemedyState.ShowModal -> {
                    showDialog(
                        childFragmentManager, GenericDialogItem(
                            it.modal.description.message,
                            null,
                            TextLocalized(it.modal.title, 0),
                            TextLocalized(it.modal.description, 0),
                            Actionable(
                                it.modal.mainButton.label, null,
                                ActionType.valueOf(it.modal.mainButton.action?.name.toString())
                            ),
                            it.modal.secondaryButton?.let { secondaryButton ->
                                Actionable(
                                    secondaryButton.label,
                                    null,
                                    ActionType.valueOf(secondaryButton.action?.name.toString())
                                )
                            }
                        )
                    )
                }

                is RemedyState.Pay -> {
                    listener?.payFromModal()
                }
            }
        }
    }

    override fun onAction(genericDialogAction: GenericDialogAction) {
        super.onAttach(context!!)
        if (genericDialogAction is GenericDialogAction.CustomAction) {
            when (genericDialogAction.type) {
                ActionType.PAY -> {
                    viewModel.onButtonPressed(PaymentResultButton.Action.MODAL_PAY)
                }

                ActionType.CHANGE_PM -> {
                    viewModel.onButtonPressed(PaymentResultButton.Action.MODAL_CHANGE_PM)
                }

                ActionType.DISMISS -> {
                    viewModel.trackRemedyModalAbort()
                }

                else -> Unit
            }
        }
    }

    companion object {
        const val TAG = "remedies"
        private const val PAYMENT_MODEL = "payment_model"
        private const val REMEDIES_MODEL = "remedies_model"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment RemediesFragment.
         */
        @JvmStatic
        fun newInstance(paymentModel: PaymentModel, remediesModel: RemediesModel) = RemediesFragment().apply {
            arguments = Bundle().apply {
                putParcelable(PAYMENT_MODEL, paymentModel)
                putParcelable(REMEDIES_MODEL, remediesModel)
            }
        }
    }

    interface Listener {
        fun enablePayButton()
        fun disablePayButton()
        fun onUserValidation()
        fun changePaymentMethod()
        fun payFromModal()
    }
}
