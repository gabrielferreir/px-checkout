package com.mercadopago.android.px.internal.features.security_code

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.meli.android.carddrawer.model.CardDrawerView
import com.mercadolibre.android.andesui.snackbar.action.AndesSnackbarAction
import com.mercadolibre.android.andesui.textfield.AndesTextfieldCode
import com.mercadolibre.android.andesui.textfield.style.AndesTextfieldCodeStyle
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.BackHandler
import com.mercadopago.android.px.core.presentation.extensions.nonNullObserve
import com.mercadopago.android.px.internal.base.BaseFragment
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.extensions.postDelayed
import com.mercadopago.android.px.internal.extensions.runWhenLaidOut
import com.mercadopago.android.px.internal.extensions.showSnackBar
import com.mercadopago.android.px.internal.features.Constants
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButtonFragment
import com.mercadopago.android.px.internal.features.pay_button.PayButton
import com.mercadopago.android.px.internal.features.security_code.model.SecurityCodeParams
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.view.animator.SecurityCodeTransition
import com.mercadopago.android.px.internal.viewmodel.FlowConfigurationModel
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

private const val ARG_PARAMS = "security_code_params"
private const val CVV_IS_FULL = "cvv_is_full"

internal class SecurityCodeFragment : BaseFragment(), PayButton.Handler, BackHandler {

    private val securityCodeViewModel: SecurityCodeViewModel by viewModel()

    private lateinit var cvvEditText: AndesTextfieldCode
    private lateinit var cvvTitle: TextView
    private lateinit var confirmButton: ConfirmButton.View
    private lateinit var confirmButtonContainer: FragmentContainerView
    private lateinit var cvvToolbar: Toolbar
    private lateinit var cardDrawer: CardDrawerView
    private lateinit var cvvSubtitle: TextView
    private lateinit var transition: SecurityCodeTransition
    private var shouldAnimate = true
    private var backEnabled = false
    private var cvvIsFull = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        arguments?.getParcelable<SecurityCodeParams>(ARG_PARAMS)?.let {

            val view = inflater.inflate(
                when (it.renderMode) {
                    RenderMode.LOW_RES -> R.layout.px_fragment_security_code_lowres
                    RenderMode.MEDIUM_RES -> R.layout.px_fragment_security_code_mediumres
                    else -> R.layout.px_fragment_security_code
                },
                container, false
            ) as ConstraintLayout

            cvvToolbar = view.findViewById(R.id.cvv_toolbar)
            cardDrawer = view.findViewById(R.id.card_drawer)
            cvvEditText = view.findViewById(R.id.cvv_edit_text)
            cvvTitle = view.findViewById(R.id.cvv_title)
            cvvSubtitle = view.findViewById(R.id.cvv_subtitle)
            confirmButtonContainer = view.findViewById(R.id.confirm_button_container)

            transition = SecurityCodeTransition(view, cardDrawer, cvvToolbar, cvvTitle, cvvSubtitle, cvvEditText,
                confirmButtonContainer)

            if (it.renderMode == RenderMode.NO_CARD) {
                cardDrawer.visibility = GONE
                cvvSubtitle.visibility = VISIBLE
            }

            return view
        } ?: error("Arguments should not be null")
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if (shouldAnimate) {
            if (enter) {
                transition.listener = object : SecurityCodeTransition.Listener {
                    override fun onTitleAnimationEnd() {
                        postAnimationConfig()
                    }

                    override fun onAnimationEnd() {
                        backEnabled = true
                    }
                }
                transition.playEnter()
            } else {
                transition.playExit()
            }
        } else {
            shouldAnimate = true
            backEnabled = true
            cvvEditText.runWhenLaidOut {
                cardDrawer.pivotX = cardDrawer.measuredWidth * 0.5f
                cardDrawer.pivotY = 0f
                cardDrawer.scaleX = 0.5f
                cardDrawer.scaleY = 0.5f
                postAnimationConfig()
            }
        }

        return super.onCreateAnimator(transit, enter, nextAnim)
    }

    private fun postAnimationConfig() {
        ConstraintSet().apply {
            val constraint = view as ConstraintLayout
            clone(constraint)
            connect(cardDrawer.id, ConstraintSet.TOP, cvvSubtitle.id, ConstraintSet.BOTTOM)
            applyTo(constraint)
            cardDrawer.showSecurityCode()
            ViewUtils.openKeyboard(cvvEditText)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            cvvIsFull = it.getBoolean(CVV_IS_FULL, false)
            transition.fromBundle(it)
            shouldAnimate = false
        }

        (activity as? AppCompatActivity?)?.apply {
            setSupportActionBar(cvvToolbar)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                setHomeButtonEnabled(true)
                cvvToolbar.setNavigationOnClickListener { onBackPressed() }
            }
        }

        arguments?.getParcelable<SecurityCodeParams>(ARG_PARAMS)?.let {
            securityCodeViewModel.init(it.paymentConfiguration, it.card, it.paymentRecovery, it.reason)
        } ?: error("Arguments should not be null")

        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        transition.toBundle(outState)
        outState.putBoolean(CVV_IS_FULL, cvvIsFull)
    }

    private fun observeViewModel() {
        with(securityCodeViewModel) {
            securityCodeViewModel.flowConfigurationLiveData.nonNullObserve(viewLifecycleOwner, ::configureViews)
            displayModelLiveData.nonNullObserve(viewLifecycleOwner) { model ->
                with(cardDrawer) {
                    model.cardUiConfiguration?.let {
                        card.name = it.name
                        card.expiration = it.date
                        card.number = it.number
                        show(it)
                    } ?: run {
                        cardDrawer.visibility = GONE
                        cvvSubtitle.visibility = VISIBLE
                    }
                }

                context?.let { context ->
                    cvvTitle.text = model.title.get(context)
                    cvvSubtitle.text = model.message.get(context)
                }

                cvvEditText.style = if (model.securityCodeLength == 4) {
                    AndesTextfieldCodeStyle.FOURSOME
                } else {
                    AndesTextfieldCodeStyle.THREESOME
                }
            }

            tokenizeErrorApiLiveData.nonNullObserve(viewLifecycleOwner) {
                val action = AndesSnackbarAction(
                    getString(R.string.px_snackbar_error_action), View.OnClickListener {
                        activity?.onBackPressed()
                    })
                view.showSnackBar(getString(R.string.px_error_title), andesSnackbarAction = action)
            }
        }
    }

    private fun configureViews(flowConfigurationModel: FlowConfigurationModel) {
        val currentFragment = childFragmentManager
            .findFragmentByTag(ConfirmButtonFragment.TAG)
            as ConfirmButton.View?
        if (currentFragment == null) {
            confirmButton = flowConfigurationModel.confirmButton
            childFragmentManager
                .beginTransaction()
                .add(R.id.confirm_button_container, confirmButton as Fragment, ConfirmButtonFragment.TAG)
                .commitAllowingStateLoss()
        } else {
            confirmButton = currentFragment
        }

        with(cvvEditText) {
            setOnCompleteListener(object : AndesTextfieldCode.OnCompletionListener {
                override fun onComplete(isFull: Boolean) {
                    cvvIsFull = isFull
                    if (cvvIsFull) confirmButton.enable() else confirmButton.disable()
                }
            })
            setOnTextChangeListener(object : AndesTextfieldCode.OnTextChangeListener {
                override fun onChange(text: String) {
                    cardDrawer.card.secCode = text
                }
            })
        }

        confirmButton.disable()
    }

    override fun getViewTrackPath(callback: ConfirmButton.ViewTrackPathCallback) {
        securityCodeViewModel.onGetViewTrackPath(callback)
    }

    override fun onPreProcess(callback: ConfirmButton.OnReadyForProcessCallback) {
        securityCodeViewModel.handlePrepayment(callback)
    }

    override fun onEnqueueProcess(callback: ConfirmButton.OnEnqueueResolvedCallback) {
        securityCodeViewModel.enqueueOnExploding(cvvEditText.text.toString(), callback)
    }

    override fun onProcessError(error: MercadoPagoError) {
        securityCodeViewModel.onPaymentError()
    }

    override fun onPostCongrats(resultCode: Int, data: Intent?) {
        activity.takeIf { it is SecurityCodeActivity }?.apply {
            setResult(resultCode, data)
            finish()
        }
    }

    override fun onPostPaymentAction(postPaymentAction: PostPaymentAction) {
        fragmentCommunicationViewModel?.postPaymentActionLiveData?.value = postPaymentAction
        if (activity is SecurityCodeActivity) {
            activity?.apply {
                setResult(Constants.RESULT_ACTION, postPaymentAction.addToIntent(Intent()))
                finish()
            }
        } else {
            activity?.supportFragmentManager?.apply {
                beginTransaction().apply {
                    remove(this@SecurityCodeFragment)
                    commit()
                }
                popBackStack()
            }
        }
    }

    override fun handleBack(): Boolean {
        if (backEnabled && !confirmButton.isExploding()) {
            securityCodeViewModel.onBack()
            transition.prepareForExit()
            ViewUtils.hideKeyboard(activity)
            postDelayed(100) {
                forceBack()
            }
        }
        return true
    }

    companion object {
        const val TAG = "security_code"

        @JvmStatic
        fun newInstance(params: SecurityCodeParams): SecurityCodeFragment {
            return SecurityCodeFragment().also {
                it.arguments = Bundle().apply {
                    putParcelable(ARG_PARAMS, params)
                }
            }
        }
    }
}