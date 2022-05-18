package com.mercadopago.android.px.tracking.internal.mapper;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.mappers.Mapper;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.tracking.internal.model.AvailableMethod;
import com.mercadopago.android.px.tracking.internal.model.CardExtraInfo;
import java.util.Set;

public class FromUserSelectionToAvailableMethod extends Mapper<UserSelectionRepository, AvailableMethod> {

    @NonNull private final Set<String> escCardIds;

    public FromUserSelectionToAvailableMethod(@NonNull final Set<String> escCardIds) {
        this.escCardIds = escCardIds;
    }

    @Override
    public AvailableMethod map(@NonNull final UserSelectionRepository val) {
        final PaymentMethod paymentMethod = val.getPaymentMethod();
        final Card card = val.getCard();

        if (PaymentTypes.isCardPaymentType(paymentMethod.getPaymentTypeId()) && card != null) {
            return new AvailableMethod(paymentMethod.getId(), paymentMethod.getPaymentTypeId(),
                CardExtraInfo.savedCard(card, val.getPayerCost(),
                    escCardIds.contains(card.getId())).toMap());
        } else if (PaymentTypes.isCardPaymentType(paymentMethod.getPaymentTypeId())) {
            return new AvailableMethod(paymentMethod.getId(), paymentMethod.getPaymentTypeId(),
                CardExtraInfo.nonSavedCardInfo(val.getIssuer(), val.getPayerCost()).toMap());
        } else {
            return new AvailableMethod(paymentMethod.getId(), paymentMethod.getPaymentTypeId());
        }
    }
}
