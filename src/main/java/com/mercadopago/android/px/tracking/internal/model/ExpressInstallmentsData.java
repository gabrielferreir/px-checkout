package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.internal.ExpressPaymentMethod;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Keep
public final class ExpressInstallmentsData extends TrackingMapModel {

    @NonNull private final String paymentMethodType;
    @NonNull private final String paymentMethodId;
    @NonNull private final Long issuerId;
    @NonNull private final String cardId;
    @NonNull private final List<PayerCostInfo> availableInstallments;

    private ExpressInstallmentsData(@NonNull final String paymentMethodType, @NonNull final String paymentMethodId,
        @NonNull final Long issuerId,
        @NonNull final String cardId,
        @NonNull final List<PayerCostInfo> payerCostTrackModels) {
        this.paymentMethodType = paymentMethodType;
        this.paymentMethodId = paymentMethodId;
        this.issuerId = issuerId;
        this.cardId = cardId;
        availableInstallments = payerCostTrackModels;
    }

    public static ExpressInstallmentsData createFrom(@NonNull final ExpressPaymentMethod expressMetadata,
        @NonNull final AmountConfiguration amountConfiguration) {
        final String paymentMethodType = expressMetadata.getPaymentTypeId();
        final String paymentMethodId = expressMetadata.getPaymentMethodId();
        final String cardId = expressMetadata.isCard() ? expressMetadata.getCard().getId() : TextUtil.EMPTY;
        final Long issuerId = expressMetadata.isCard() ? expressMetadata.getCard().getDisplayInfo().getIssuerId() : -1;
        final List<PayerCostInfo> payerCostTrackModels = new ArrayList<>();
        for (final PayerCost payerCost : amountConfiguration.getPayerCosts()) {
            payerCostTrackModels.add(new PayerCostInfo(payerCost));
        }

        return new ExpressInstallmentsData(paymentMethodType, paymentMethodId, issuerId, cardId, payerCostTrackModels);
    }
}
