package com.mercadopago.android.px.internal.features.payment_result.remedies.view

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.di.MapperProvider
import com.mercadopago.android.px.internal.experiments.BadgeVariant
import com.mercadopago.android.px.internal.extensions.gone
import com.mercadopago.android.px.internal.extensions.visible
import com.mercadopago.android.px.internal.features.TermsAndConditionsActivity
import com.mercadopago.android.px.internal.features.one_tap.slider.PaymentMethodFragment
import com.mercadopago.android.px.internal.features.one_tap.slider.TitlePagerAdapter
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesPayerCost
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesPaymentMethodMapper
import com.mercadopago.android.px.internal.view.LinkableTextView
import com.mercadopago.android.px.internal.view.MPTextView
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorView
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.model.internal.Text
import com.mercadopago.android.px.model.internal.remedies.CardSize
import kotlinx.android.parcel.Parcelize

private const val NO_SELECTED = -1

internal class RetryPaymentFragment : Fragment(), PaymentMethodFragment.DisabledDetailDialogLauncher {

    private lateinit var message: TextView
    private lateinit var cvvRemedy: CvvRemedy
    private lateinit var paymentMethodDescriptor: PaymentMethodDescriptorView
    private lateinit var paymentMethodTitle: MPTextView
    private lateinit var bottomText: LinkableTextView
    private val remediesLinkableMapper = MapperProvider.remediesLinkableMapper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_remedies_retry_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        message = view.findViewById(R.id.message)
        cvvRemedy = view.findViewById(R.id.cvv_remedy)
        paymentMethodDescriptor = view.findViewById(R.id.payment_method_descriptor)
        paymentMethodTitle = view.findViewById(R.id.payment_method_title)
        bottomText = view.findViewById(R.id.bottom_text)
    }

    fun init(model: Model, methodData: OneTapItem?) {
        message.text = model.message
        methodData?.let {
            addCard(it, model.cardSize)
            if (model.isAnotherMethod) {
                model.bottomMessage?.let { message -> paymentMethodTitle.setText(message) }
                showPaymentMethodDescriptor(it, model.payerCost)
            }

            it.consumerCredits?.let { consumerCredits ->
                with(bottomText) {
                    updateModel(remediesLinkableMapper.mapRemedies(consumerCredits.displayInfo.bottomText))
                    setLinkableTextListener {
                        val instalmentKey = model.payerCost?.installments ?: NO_SELECTED
                        context?.apply {
                            TermsAndConditionsActivity.start(this, it.getLinkByInstallment(instalmentKey))
                        }
                    }
                }
            }
        }
        model.cvvModel?.let { cvvRemedy.init(it) } ?: cvvRemedy.gone()
    }

    fun setListener(listener: CvvRemedy.Listener) {
        cvvRemedy.listener = listener
    }

    private fun addCard(methodData: OneTapItem, cardSize: CardSize?) {
        childFragmentManager.beginTransaction().apply {
            val drawableFragmentItem = MapperProvider.getPaymentMethodDrawableItemMapper().map(methodData)!!
            drawableFragmentItem.switchModel = null
            val paymentMethodFragment = RemediesPaymentMethodMapper(cardSize).map(drawableFragmentItem)
            paymentMethodFragment.onFocusIn()
            replace(R.id.card_container, paymentMethodFragment)
            commitAllowingStateLoss()
        }
    }

    private fun showPaymentMethodDescriptor(methodData: OneTapItem, payerCost: RemediesPayerCost?) {
        paymentMethodDescriptor.visible()
        paymentMethodTitle.visible()
        if (!paymentMethodTitle.text.contains(":")) paymentMethodTitle.append(":") // FIXME
        val model = MapperProvider.getPaymentMethodDescriptorMapper().map(methodData).getCurrent()
        model.formatForRemedy()
        payerCost?.let { model.setCurrentPayerCost(it.payerCostIndex) }
        paymentMethodDescriptor.configureExperiment(BadgeVariant().default)
        paymentMethodDescriptor.update(model)
    }

    @Parcelize
    internal data class Model(
        val message: String, val isAnotherMethod: Boolean, val cardSize: CardSize?, val cvvModel: CvvRemedy.Model?,
        val bottomMessage: Text? = null, var payerCost: RemediesPayerCost? = null
    ) : Parcelable
}
