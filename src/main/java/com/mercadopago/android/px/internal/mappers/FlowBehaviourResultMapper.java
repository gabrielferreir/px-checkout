package com.mercadopago.android.px.internal.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.FlowBehaviour;
import com.mercadopago.android.px.internal.util.StatusHelper;
import com.mercadopago.android.px.model.IPaymentDescriptor;

public class FlowBehaviourResultMapper extends Mapper<IPaymentDescriptor, FlowBehaviour.Result> {

    @Override
    public FlowBehaviour.Result map(@NonNull final IPaymentDescriptor payment) {
        if (StatusHelper.isSuccess(payment)) {
            return FlowBehaviour.Result.SUCCESS;
        } else if (StatusHelper.isRejected(payment)) {
            return FlowBehaviour.Result.FAILURE;
        }
        return FlowBehaviour.Result.PENDING;
    }
}