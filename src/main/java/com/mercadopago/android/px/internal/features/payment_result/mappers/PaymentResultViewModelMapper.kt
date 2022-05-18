package com.mercadopago.android.px.internal.features.payment_result.mappers

import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionMapper
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultLegacyViewModel
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultViewModel
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.internal.viewmodel.PaymentModel

internal class PaymentResultViewModelMapper(
    private val configuration: PaymentResultScreenConfiguration,
    private val factory: PaymentResultViewModelFactory,
    private val instructionMapper: InstructionMapper,
    private val autoReturnFromPreference: String?,
    private val paymentResultBodyModelMapper: PaymentResultBodyModelMapper
) : Mapper<PaymentModel, PaymentResultViewModel>() {

    override fun map(model: PaymentModel): PaymentResultViewModel {
        val bodyModel = paymentResultBodyModelMapper.map(model)
        val legacyViewModel = PaymentResultLegacyViewModel(
            model, bodyModel.paymentResultMethodModels, configuration)
        val remediesModel = PaymentResultRemediesModelMapper.map(model.remedies)
        return PaymentResultViewModel(
            PaymentResultHeaderModelMapper(
                configuration, factory, model.congratsResponse.instructions, remediesModel).map(model),
            remediesModel,
            PaymentResultFooterModelMapper.map(model),
            bodyModel, legacyViewModel,
            model.congratsResponse.instructions?.let { instructionMapper.map(it) },
            CongratsAutoReturnMapper(autoReturnFromPreference, model.paymentResult.paymentStatus)
                .map(model.congratsResponse.autoReturn))
    }
}
