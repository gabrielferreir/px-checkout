package com.mercadopago.android.px.internal.features.one_tap.confirm_button

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mercadolibre.android.andesui.snackbar.action.AndesSnackbarAction
import com.mercadolibre.android.andesui.snackbar.duration.AndesSnackbarDuration
import com.mercadolibre.android.andesui.snackbar.type.AndesSnackbarType
import com.mercadolibre.android.ui.widgets.MeliButton
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.base.BaseFragment
import com.mercadopago.android.px.internal.base.BaseState
import com.mercadopago.android.px.internal.extensions.showSnackBar
import com.mercadopago.android.px.internal.features.explode.ExplodeDecorator
import com.mercadopago.android.px.internal.features.explode.ExplodingFragment
import com.mercadopago.android.px.internal.features.pay_button.UIError
import com.mercadopago.android.px.internal.util.ErrorUtil
import com.mercadopago.android.px.internal.util.FragmentUtil
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.util.nonNullObserve
import com.mercadopago.android.px.internal.view.OnSingleClickListener
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker

private const val EXTRA_STATE = "extra_state"
private const val EXTRA_VISIBILITY = "extra_visibility"

internal abstract class ConfirmButtonFragment<
    S : BaseState,
    H : ConfirmButton.Handler
    > : BaseFragment(), ConfirmButton.View {

    protected abstract val viewModel: ConfirmButtonViewModel<S, H>
    private var buttonStatus = MeliButton.State.NORMAL
    protected lateinit var button: MeliButton
    private var confirmButtonStateChange: ConfirmButton.StateChange = object : ConfirmButton.StateChange {
        override fun overrideStateChange(uiState: ConfirmButton.State): Boolean {
            return false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_fragment_confirm_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachHandler(resolveHandler())

        button = view.findViewById(R.id.confirm_button)

        button.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                viewModel.onButtonPressed()
            }
        })

        savedInstanceState?.let {
            buttonStatus = it.getInt(EXTRA_STATE, MeliButton.State.NORMAL)
            button.visibility = it.getInt(EXTRA_VISIBILITY, View.VISIBLE)
            val state = it.getParcelable<S>(BUNDLE_STATE)!!
            viewModel.restoreState(state)
        }

        updateButtonState()

        viewModel.buttonTextLiveData.nonNullObserve(viewLifecycleOwner) {
            button.text = it.getButtonText(requireContext())
        }
    }

    @SuppressWarnings("UNCHECKED_CAST")
    private fun resolveHandler(): H {
        return when {
            targetFragment is ConfirmButton.Handler -> targetFragment
            parentFragment is ConfirmButton.Handler -> parentFragment
            context is ConfirmButton.Handler -> context
            else -> throw IllegalStateException("Parent should implement ${ConfirmButton.Handler::class.java.simpleName}")
        } as H
    }

    override fun enable() {
        val handleState = confirmButtonStateChange.overrideStateChange(ConfirmButton.State.ENABLE)
        if (!handleState) {
            buttonStatus = MeliButton.State.NORMAL
            updateButtonState()
        }
    }

    override fun disable() {
        val handleState = confirmButtonStateChange.overrideStateChange(ConfirmButton.State.DISABLE)
        if (!handleState) {
            buttonStatus = MeliButton.State.DISABLED
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        if (::button.isInitialized) {
            button.state = buttonStatus
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_STATE, buttonStatus)
        if (this::button.isInitialized) {
            outState.putInt(EXTRA_VISIBILITY, button.visibility)
        }
        outState.putParcelable(BUNDLE_STATE, viewModel.state)
    }

    protected fun startLoadingButton(paymentTimeout: Int, buttonConfig: PayButtonViewModel) {
        context?.let {
            button.post {
                if (!isAdded) {
                    viewModel.track(
                        FrictionEventTracker.with(
                            "${TrackWrapper.BASE_PATH}/confirm_button_loading",
                            FrictionEventTracker.Id.GENERIC,
                            FrictionEventTracker.Style.SCREEN,
                            emptyMap<String, String>()
                        )
                    )
                } else {
                    val handleState = confirmButtonStateChange.overrideStateChange(ConfirmButton.State.IN_PROGRESS)
                    if (!handleState) {
                        val explodingFragment = ExplodingFragment.newInstance(
                            buttonConfig.getButtonProgressText(it), paymentTimeout
                        )
                        childFragmentManager.beginTransaction()
                            .add(R.id.exploding_frame, explodingFragment, ExplodingFragment.TAG)
                            .commitNowAllowingStateLoss()
                        hideConfirmButton()
                    }
                }
            }
        }
    }

    private fun hideConfirmButton() {
        with(button) {
            clearAnimation()
            visibility = View.INVISIBLE
        }
    }

    private fun showConfirmButton() {
        with(button) {
            clearAnimation()
            visibility = View.VISIBLE
        }
    }

    protected fun cancelLoading() {
        showConfirmButton()
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentByTag(ExplodingFragment.TAG) as ExplodingFragment?
        fragment?.takeIf { it.isAdded && it.hasFinished() }?.apply {
            fragmentManager
                .beginTransaction()
                .remove(fragment)
                .commitNowAllowingStateLoss()
        }
        restoreStatusBar()
        enable()
    }

    protected fun finishLoading(params: ExplodeDecorator? = null) {
        ViewUtils.hideKeyboard(activity)
        childFragmentManager.findFragmentByTag(ExplodingFragment.TAG)
            ?.let { (it as ExplodingFragment).finishLoading(params) }
            ?: onAnimationFinished()
    }

    private fun restoreStatusBar() {
        activity?.let {
            ViewUtils.setStatusBarColor(ContextCompat.getColor(it, R.color.px_colorPrimaryDark), it.window)
        }
    }

    protected fun resolveError(uiError: UIError) {
        when (uiError) {
            is UIError.ConnectionError -> resolveConnectionError(uiError)
            is UIError.NotRecoverableError ->
                ErrorUtil.startErrorActivity(this,
                    MercadoPagoError.createNotRecoverable(uiError.error.message.orEmpty()))
            else -> {
                val action = AndesSnackbarAction(
                    getString(R.string.px_snackbar_error_action), View.OnClickListener {
                        activity?.onBackPressed()
                    })
                view.showSnackBar(getString(R.string.px_error_title), andesSnackbarAction = action)
            }
        }
    }

    private fun resolveConnectionError(uiError: UIError.ConnectionError) {
        var action: AndesSnackbarAction? = null
        var type = AndesSnackbarType.NEUTRAL
        var duration = AndesSnackbarDuration.SHORT

        uiError.actionMessage?.apply {
            action = AndesSnackbarAction(getString(this), View.OnClickListener { activity?.onBackPressed() })
            type = AndesSnackbarType.ERROR
            duration = AndesSnackbarDuration.LONG
        }

        view.showSnackBar(getString(uiError.message), type, duration, action)
    }

    override fun onAnimationFinished() = Unit

    override fun onResultIconAnimation() = Unit

    override fun shouldSkipRevealAnimation() = true

    override fun getParentView() = button

    override fun addOnStateChange(stateChange: ConfirmButton.StateChange) {
        this.confirmButtonStateChange = stateChange
    }

    override fun onDestroy() {
        FragmentUtil.tryRemoveNow(childFragmentManager, ExplodingFragment.TAG)
        super.onDestroy()
    }

    private fun attachHandler(handler: H) {
        viewModel.attach(handler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.detach()
    }

    override fun isExploding(): Boolean {
        return FragmentUtil.isFragmentVisible(childFragmentManager, ExplodingFragment.TAG)
    }

    companion object {
        const val TAG = "confirm_button_fragment"
    }
}
