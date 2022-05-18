package com.mercadopago.android.px.internal.features.business_result

import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.features.payment_congrats.model.CongratsViewModelMapper
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsResponse
import com.mercadopago.android.px.internal.features.payment_result.CongratsAutoReturn
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultButton
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultFooter
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.internal.mappers.PaymentResultMethodMapper
import com.mercadopago.android.px.internal.view.PaymentResultBody
import com.mercadopago.android.px.internal.view.PaymentResultHeader
import com.mercadopago.android.px.internal.view.PaymentResultMethod
import com.mercadopago.android.px.internal.viewmodel.GenericLocalized
import com.mercadopago.android.px.internal.viewmodel.PaymentResultType
import com.mercadopago.android.px.tracking.internal.MPTracker
import java.util.ArrayList

internal class BusinessPaymentResultMapper(
    private val tracker: MPTracker,
    private val paymentResultMethodMapper: PaymentResultMethodMapper
) :
    Mapper<PaymentCongratsModel, BusinessPaymentResultViewModel>() {

    override fun map(model: PaymentCongratsModel): BusinessPaymentResultViewModel {
        return BusinessPaymentResultViewModel(
            getHeaderModel(model),
            getBodyModel(model),
            getFooterModel(model),
            getAutoReturnModel(model.paymentCongratsResponse?.autoReturn)
        )
    }

    private fun getBodyModel(model: PaymentCongratsModel): PaymentResultBody.Model {
        val paymentResultMethodModels: MutableList<PaymentResultMethod.Model> = ArrayList()
        if (model.shouldShowPaymentMethod == true) {
            for (paymentInfo in model.paymentsInfo) {
                paymentResultMethodModels.add(paymentResultMethodMapper.map(paymentInfo, model.statementDescription))
            }
        }

        return PaymentResultBody.Model.Builder()
            .setPaymentResultMethodModels(paymentResultMethodModels)
            .apply { model.paymentCongratsResponse?.let {
                setCongratsViewModel(CongratsViewModelMapper(BusinessPaymentResultTracker(tracker)).map(it))
            }}
            .apply {
                if (model.shouldShowReceipt == true) {
                    if (model.forceShowReceipt || model.congratsType == PaymentCongratsModel.CongratsType.APPROVED) {
                        setReceiptId(model.paymentId.toString())
                    }
                }
            }
            .setHelp(model.help)
            .setStatement(model.statementDescription)
            .setTopFragment(model.topFragment)
            .setBottomFragment(model.bottomFragment)
            .setImportantFragment(model.importantFragment)
            .build()
    }

    private fun getHeaderModel(model: PaymentCongratsModel): PaymentResultHeader.Model {
        val type = PaymentResultType.from(model.congratsType)
        return PaymentResultHeader.Model.Builder()
            .setIconUrl(model.imageUrl)
            .setIconImage(if (model.iconId == 0) R.drawable.px_icon_product else model.iconId)
            .setBackground(type.resColor)
            .setBadgeImage(type.badge)
            .setTitle(GenericLocalized(model.title, 0))
            .setLabel(GenericLocalized(model.subtitle, type.message))
            .build()
    }

    private fun getFooterModel(model: PaymentCongratsModel): PaymentResultFooter.Model {
        return PaymentResultFooter.Model(
            model.footerMainAction?.let {
                PaymentResultButton(PaymentResultButton.Type.LOUD, it)
            },
            model.footerSecondaryAction?.let {
                PaymentResultButton(PaymentResultButton.Type.TRANSPARENT, it)
            }
        )
    }

    private fun getAutoReturnModel(autoReturn: PaymentCongratsResponse.AutoReturn?): CongratsAutoReturn.Model? {
        return autoReturn?.let {
            CongratsAutoReturn.Model(it.label, it.seconds)
        }
    }
}
