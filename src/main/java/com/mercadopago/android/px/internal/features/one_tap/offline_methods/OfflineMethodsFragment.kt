package com.mercadopago.android.px.internal.features.one_tap.offline_methods

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.BackHandler
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.extensions.addOnLaidOutListener
import com.mercadopago.android.px.internal.extensions.invisible
import com.mercadopago.android.px.internal.extensions.setHeight
import com.mercadopago.android.px.internal.extensions.visible
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButtonFragment
import com.mercadopago.android.px.internal.font.PxFont
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.util.nonNullObserve
import com.mercadopago.android.px.internal.view.MPTextView
import com.mercadopago.android.px.internal.viewmodel.AmountLocalized
import com.mercadopago.android.px.model.internal.PaymentConfiguration

internal class OfflineMethodsFragment : Fragment(), OfflineMethods.View, BackHandler {

    private val viewModel by viewModel<OfflineMethodsViewModel>()

    private var fadeInAnimation: Animation? = null
    private var fadeOutAnimation: Animation? = null

    private lateinit var panIndicator: View
    private lateinit var confirmButtonContainer: FragmentContainerView
    private lateinit var confirmButton: ConfirmButton.View
    private lateinit var totalAmountTextView: TextView
    private lateinit var header: View
    private lateinit var footer: View
    private lateinit var fakeFooter: View
    private lateinit var bottomDescription: MPTextView
    private lateinit var adapter: OfflineMethodsAdapter

    private lateinit var bottomSheet: View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_fragment_offline_methods, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view) {
            header = findViewById(R.id.header)
            footer = findViewById(R.id.bottom_sheet_footer)
            fakeFooter = findViewById(R.id.fake_footer)
            panIndicator = findViewById(R.id.pan_indicator)
            bottomSheet = findViewById(R.id.offline_methods_bottom_sheet)
            totalAmountTextView = findViewById(R.id.total_amount)
            bottomDescription = findViewById(R.id.bottom_description)
            confirmButtonContainer = view.findViewById(R.id.confirm_button_container)
            configureRecycler(findViewById(R.id.methods))
        }

        configureBottomSheet(savedInstanceState)

        with(viewModel) {
            offlineMethodsModelLiveData.nonNullObserve(viewLifecycleOwner, ::draw)
            flowConfigurationLiveData.nonNullObserve(viewLifecycleOwner) {
                val currentFragment = childFragmentManager
                    .findFragmentByTag(ConfirmButtonFragment.TAG)
                    as ConfirmButton.View?
                if (currentFragment == null) {
                    confirmButton = it.confirmButton

                    childFragmentManager
                        .beginTransaction()
                        .add(R.id.confirm_button_container, confirmButton as Fragment, ConfirmButtonFragment.TAG)
                        .commitAllowingStateLoss()
                } else {
                    confirmButton = currentFragment
                }

                confirmButton.disable()

            }
            deepLinkLiveData.nonNullObserve(viewLifecycleOwner) { startKnowYourCustomerFlow(it) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (this::bottomSheetBehavior.isInitialized) {
            outState.putInt(EXTRA_BOTTOM_SHEET_STATE, bottomSheetBehavior.state)
        }
    }

    private fun draw(model: OfflineMethods.Model) {
        ViewUtils.loadOrHide(View.GONE, model.bottomDescription, bottomDescription)
        footer.addOnLaidOutListener { view -> fakeFooter.setHeight(view.height) }
        updateTotalView(model.amountLocalized)
        adapter.setItems(FromOfflinePaymentTypesMetadataToOfflineItems.map(model.offlinePaymentTypes))
    }

    private fun configureBottomSheet(savedInstanceState: Bundle?) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, state: Int) {}
            override fun onSlide(view: View, offset: Float) {
                header.alpha = if (offset >= 0) offset else 0f
                footer.alpha = if (offset >= 0) 1f else if (offset <= -0.5) 0f else 1 + offset * 2
                header.alpha.takeIf { it == 0f }?.run { header.invisible() } ?: run { header.visible() }
                footer.alpha.takeIf { it == 0f }?.run { footer.invisible() } ?: run { footer.visible() }
            }
        })

        savedInstanceState?.let {
            it.getInt(EXTRA_BOTTOM_SHEET_STATE).let { state ->
                bottomSheetBehavior.state = state
                if (state == BottomSheetBehavior.STATE_HIDDEN) footer.invisible() else footer.visible()
                if (state == BottomSheetBehavior.STATE_EXPANDED) header.visible() else header.invisible()
            }
        } ?: run { bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN }
    }

    private fun configureRecycler(recycler: RecyclerView) {
        val linearLayoutManager = LinearLayoutManager(context).also { recycler.layoutManager = it }
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val atTop = !recyclerView.canScrollVertically(TOP)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> if (atTop) {
                        panIndicator.clearAnimation()
                        panIndicator.startAnimation(fadeOutAnimation)
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> if (atTop) {
                        panIndicator.clearAnimation()
                        panIndicator.startAnimation(fadeInAnimation)
                    }
                }
            }
        })
        context?.let {
            val decoration = DividerItemDecoration(it, linearLayoutManager.orientation)
            decoration.setDrawable(ContextCompat.getDrawable(it, R.drawable.px_item_decorator_divider)!!)
            recycler.addItemDecoration(decoration)
        }
        adapter = OfflineMethodsAdapter(object : OfflineMethods.OnMethodSelectedListener {
            override fun onItemSelected(selectedItem: OfflineMethodItem) {
                viewModel.onMethodSelected(selectedItem)
                confirmButton.enable()
            }
        }).also { recycler.adapter = it }
    }

    private fun updateTotalView(amountLocalized: AmountLocalized) {
        val editable: Editable = SpannableStringBuilder()
        val editableDescription: Editable = SpannableStringBuilder()
        val totalText = getString(R.string.px_review_summary_total)
        context?.let {
            editable.append(totalText)
                .append(TextUtil.SPACE)
                .append(amountLocalized[it])
            ViewUtils.setFontInSpannable(it, PxFont.SEMI_BOLD, editable)

            totalAmountTextView.text = editable
        }
        editableDescription
            .append(totalText)
            .append(TextUtil.SPACE)
            //TODO por que estaba en floatValue?
            .append(amountLocalized.amount.toString())
            .append(getString(R.string.px_money))
        totalAmountTextView.contentDescription = editableDescription
    }

    override fun handleBack(): Boolean {
        val isExploding = confirmButton.isExploding()
        val isVisible = bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN
        if (!isExploding && isVisible) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.onBack()
        }
        return isExploding || isVisible
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val offset = resources.getInteger(R.integer.px_long_animation_time)
        val duration = resources.getInteger(R.integer.px_shorter_animation_time)
        val animation = AnimationUtils.loadAnimation(context, if (enter) R.anim.px_fade_in else R.anim.px_fade_out)
        animation.duration = duration.toLong()
        if (enter) {
            animation.startOffset = offset.toLong()
        }
        header.startAnimation(animation)
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun showExpanded() {
        viewModel.onSheetShowed()
        bottomSheet.post { bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED }
    }

    override fun showCollapsed() {
        viewModel.onSheetShowed()
        bottomSheet.post { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
    }

    private fun startKnowYourCustomerFlow(flowLink: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(flowLink)
        startActivity(intent)
    }

    override fun getViewTrackPath(callback: ConfirmButton.ViewTrackPathCallback) {
        viewModel.onGetViewTrackPath(callback)
    }

    override fun onPreProcess(callback: ConfirmButton.OnReadyForProcessCallback) {
        viewModel.onPrePayment(callback)
    }

    override fun onProcessExecuted(configuration: PaymentConfiguration) {
        viewModel.onPaymentExecuted(configuration)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val duration = resources.getInteger(R.integer.px_shorter_animation_time)
        fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.px_fade_in)
            .apply { this.duration = duration.toLong() }
        fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.px_fade_out)
            .apply { this.duration = duration.toLong() }
    }

    override fun onDetach() {
        super.onDetach()
        fadeInAnimation = null
        fadeOutAnimation = null
    }

    companion object {
        private const val EXTRA_BOTTOM_SHEET_STATE = "bottom_sheet_state"
        private const val TOP = -1
    }
}