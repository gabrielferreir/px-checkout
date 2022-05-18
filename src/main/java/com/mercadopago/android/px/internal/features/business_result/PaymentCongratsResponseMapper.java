package com.mercadopago.android.px.internal.features.business_result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadolibre.android.andesui.message.hierarchy.AndesMessageHierarchy;
import com.mercadolibre.android.andesui.message.type.AndesMessageType;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsResponse;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText;
import com.mercadopago.android.px.internal.mappers.Mapper;
import com.mercadopago.android.px.model.internal.Button;
import com.mercadopago.android.px.model.internal.CongratsResponse;
import java.util.ArrayList;
import java.util.List;

public class PaymentCongratsResponseMapper extends Mapper<CongratsResponse, PaymentCongratsResponse> {

    @Override
    public PaymentCongratsResponse map(final CongratsResponse congratsResponse) {
        return new PaymentCongratsResponse(getLoyalty(congratsResponse.getScore()),
            getDiscount(congratsResponse.getDiscount()), getExpenseSplit(congratsResponse.getMoneySplit()),
            getCrossSelling(congratsResponse.getCrossSellings()),
            getAction(congratsResponse.getViewReceipt()),
            congratsResponse.getCustomOrder(),
            congratsResponse.getBackUrl(),
            congratsResponse.getRedirectUrl(),
            getAutoReturn(congratsResponse.getAutoReturn()),
            getOperationInfo(congratsResponse.getOperationInfo()));
    }

    private PaymentCongratsResponse.Loyalty getLoyalty(final CongratsResponse.Score score) {
        if (score != null) {
            return new PaymentCongratsResponse.Loyalty(
                new PaymentCongratsResponse.Loyalty.Progress(score.getProgress().getPercentage(),
                    score.getProgress().getColor(), score.getProgress().getLevel()), score.getTitle(),
                getAction(score.getAction()));
        }
        return null;
    }

    private List<PaymentCongratsResponse.CrossSelling> getCrossSelling(
        final Iterable<CongratsResponse.CrossSelling> crossSellings) {
        final List<PaymentCongratsResponse.CrossSelling> crossSellingList = new ArrayList<>();
        for (final CongratsResponse.CrossSelling crossSelling : crossSellings) {
            crossSellingList
                .add(new PaymentCongratsResponse.CrossSelling(crossSelling.getTitle(), crossSelling.getIcon(),
                    getAction(crossSelling.getAction()), crossSelling.getContentId()));
        }
        return crossSellingList;
    }

    private PaymentCongratsResponse.Discount getDiscount(final CongratsResponse.Discount discount) {
        if (discount != null) {
            final PaymentCongratsResponse.AdditionalEdgeInsets additionalEdgeInsets;
            final PaymentCongratsResponse.PXBusinessTouchpoint touchpoint;
            if (discount.getTouchpoint() != null) {
                additionalEdgeInsets =
                    discount.getTouchpoint().getAdditionalEdgeInsets() == null ? null :
                        new PaymentCongratsResponse.AdditionalEdgeInsets(
                            discount.getTouchpoint().getAdditionalEdgeInsets().getTop(),
                            discount.getTouchpoint().getAdditionalEdgeInsets().getLeft(),
                            discount.getTouchpoint().getAdditionalEdgeInsets().getBottom(),
                            discount.getTouchpoint().getAdditionalEdgeInsets().getRight());
                touchpoint =
                    new PaymentCongratsResponse.PXBusinessTouchpoint(discount.getTouchpoint().getId(),
                        discount.getTouchpoint().getType(), discount.getTouchpoint().getContent(),
                        discount.getTouchpoint().getTracking(), additionalEdgeInsets);
            } else {
                additionalEdgeInsets = null;
                touchpoint = null;
            }

            return new PaymentCongratsResponse.Discount(discount.getTitle(), discount.getSubtitle(),
                getAction(discount.getAction())
                , new PaymentCongratsResponse.Discount.DownloadApp(discount.getActionDownload().getTitle(),
                getAction(discount.getActionDownload().getAction())),
                touchpoint, getDiscountItems(discount.getItems()));
        }
        return null;
    }

    @Nullable
    private PaymentCongratsResponse.ExpenseSplit getExpenseSplit(
        @Nullable final CongratsResponse.MoneySplit moneySplit) {
        if (moneySplit != null) {
            return new PaymentCongratsResponse.ExpenseSplit(
                new PaymentCongratsText(moneySplit.getTitle().getMessage(),
                    moneySplit.getTitle().getBackgroundColor(), moneySplit.getTitle().getTextColor(),
                    moneySplit.getTitle().getWeight()), getAction(moneySplit.getAction()),
                    moneySplit.getImageUrl());
        }
        return null;
    }

    @NonNull
    private PaymentCongratsResponse.Action getAction(final Button button) {
        if (button != null) {
            return new PaymentCongratsResponse.Action(button.getLabel(), button.getTarget());
        }
        return null;
    }

    private List<PaymentCongratsResponse.Discount.Item> getDiscountItems(
        final List<CongratsResponse.Discount.Item> items) {
        final List<PaymentCongratsResponse.Discount.Item> discountItems = new ArrayList<>();
        for (final CongratsResponse.Discount.Item item : items) {
            discountItems.add(
                new PaymentCongratsResponse.Discount.Item(item.getTitle(), item.getSubtitle(), item.getIcon(),
                    item.getTarget(), item.getCampaignId()));
        }
        return discountItems;
    }

    @Nullable
    private PaymentCongratsResponse.AutoReturn getAutoReturn(@Nullable final CongratsResponse.AutoReturn autoReturn) {
        if (autoReturn != null) {
            return new PaymentCongratsResponse.AutoReturn(autoReturn.getLabel(), autoReturn.getSeconds());
        }
        return null;
    }

    @Nullable
    private PaymentCongratsResponse.OperationInfo getOperationInfo(@Nullable final CongratsResponse.OperationInfo operationInfo) {
        if (operationInfo != null) {
            return
                new PaymentCongratsResponse.OperationInfo(AndesMessageHierarchy.valueOf(operationInfo.getHierarchy().toUpperCase()),
                AndesMessageType.valueOf(operationInfo.getType().toUpperCase()), operationInfo.getBody());
        }
        return null;
    }
}
