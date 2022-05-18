package com.mercadopago.android.px.internal.features.payment_congrats.model;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.FlowBehaviour;
import com.mercadopago.android.px.internal.mappers.Mapper;

public class FlowBehaviourResultMapper extends Mapper<PaymentCongratsModel.CongratsType, FlowBehaviour.Result> {
    @Override
    public FlowBehaviour.Result map(@NonNull final PaymentCongratsModel.CongratsType congratsType) {
        final FlowBehaviour.Result result;
        switch (congratsType) {
            case APPROVED:
                result = FlowBehaviour.Result.SUCCESS;
                break;
            case REJECTED:
                result = FlowBehaviour.Result.FAILURE;
                break;
            default:
                result = FlowBehaviour.Result.PENDING;
                break;
        }
        return result;
    }
}
