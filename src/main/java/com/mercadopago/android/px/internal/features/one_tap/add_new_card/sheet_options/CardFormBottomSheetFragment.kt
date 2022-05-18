package com.mercadopago.android.px.internal.features.one_tap.add_new_card.sheet_options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.di.Session
import com.mercadopago.android.px.internal.extensions.postDelayed
import com.mercadopago.android.px.internal.features.one_tap.OneTapFragment.REQ_CARD_FORM_WEB_VIEW
import com.mercadopago.android.px.internal.features.one_tap.OneTapFragment.REQ_CODE_CARD_FORM
import com.mercadopago.android.px.internal.font.FontHelper
import com.mercadopago.android.px.internal.font.PxFont
import com.mercadopago.android.px.internal.util.CardFormWrapper
import com.mercadopago.android.px.model.CardFormInitType

private const val EXTRA_BOTTOM_SHEET_MODEL = "bottom_sheet_model"

internal class CardFormBottomSheetFragment : Fragment() {

    private lateinit var cardFormWrapper: CardFormWrapper
    private var cardFormOptionClick: CardFormOptionClick? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_fragment_card_form_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<CardFormBottomSheetModel>(EXTRA_BOTTOM_SHEET_MODEL)?.let { model ->
            setUpRecyclerView(view, model)
            setUpCardFormWrapper()
        } ?: error("Arguments should not be null")
    }

    private fun setUpRecyclerView(view: View, cardFormBottomSheetModel: CardFormBottomSheetModel) {
        val newCardSheetOptions = view.findViewById<RecyclerView>(R.id.sheet_options)
        val titleHeader = view.findViewById<TextView>(R.id.sheet_option_title_header)
        context?.let { context ->
            val drawableDecorator = ContextCompat.getDrawable(context, R.drawable.px_item_option_decorator_divider)
            drawableDecorator?.let {
                val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                decoration.setDrawable(it)
                newCardSheetOptions.addItemDecoration(decoration)
            }
        }
        titleHeader.text = cardFormBottomSheetModel.titleHeader
        FontHelper.setFont(titleHeader, PxFont.SEMI_BOLD)
        newCardSheetOptions.adapter = SheetOptionsRecyclerViewAdapter(
            cardFormBottomSheetModel.cardFormOptions,
            this::onCardFormOptionSelected)
    }

    private fun setUpCardFormWrapper() {
        val configurationModule = Session.getInstance().configurationModule
        cardFormWrapper = CardFormWrapper(
            configurationModule.paymentSettings,
            configurationModule.trackingRepository,
            configurationModule.authorizationProvider)
    }

    private fun onCardFormOptionSelected(cardFormInitType: CardFormInitType) {
        parentFragment?.fragmentManager?.let { fragmentManager ->

            cardFormOptionClick?.onOptionClick()

            postDelayed(200) {
                when (cardFormInitType) {
                    CardFormInitType.STANDARD -> {
                        cardFormWrapper
                            .getCardFormWithFragment()
                            .start(
                                fragmentManager,
                                REQ_CODE_CARD_FORM,
                                R.id.one_tap_fragment
                            )
                    }

                    CardFormInitType.WEB_PAY -> cardFormWrapper
                        .getCardFormWithWebView()
                        .start(parentFragment!!, REQ_CARD_FORM_WEB_VIEW)
                }
            }
        }
    }

    fun setCardFormOptionClick(cardFormOptionClick: CardFormOptionClick) {
        this.cardFormOptionClick = cardFormOptionClick
    }

    interface CardFormOptionClick {
        fun onOptionClick()
    }

    companion object {
        @JvmStatic
        fun newInstance(
            cardFormBottomSheetModel: CardFormBottomSheetModel) = CardFormBottomSheetFragment().also { fragment ->
            fragment.arguments = Bundle().also {
                it.putParcelable(EXTRA_BOTTOM_SHEET_MODEL, cardFormBottomSheetModel)
            }
        }
    }
}