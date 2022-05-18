package com.mercadopago.android.px.tracking.internal.mapper;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.mappers.Mapper;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.tracking.internal.model.AvailableMethod;
import com.mercadopago.android.px.tracking.internal.model.CardExtraInfo;
import java.util.Set;

public class FromCustomItemToAvailableMethod extends Mapper<CustomSearchItem, AvailableMethod> {

    @NonNull private final Set<String> escCardIds;

    public FromCustomItemToAvailableMethod(@NonNull final Set<String> escCardIds) {
        this.escCardIds = escCardIds;
    }

    @Override
    public AvailableMethod map(@NonNull final CustomSearchItem val) {
        if (PaymentTypes.isCardPaymentType(val.getType())) {
            return new AvailableMethod(val.getPaymentMethodId(), val.getType(),
                CardExtraInfo.customOptions(val, escCardIds.contains(val.getId())).toMap());
        } else {
            return new AvailableMethod(val.getId(), val.getType());
        }
    }
}
