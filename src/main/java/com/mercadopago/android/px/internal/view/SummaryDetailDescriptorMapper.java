package com.mercadopago.android.px.internal.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.features.AmountDescriptorViewModelFactory;
import com.mercadopago.android.px.internal.mappers.Mapper;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.util.ChargeRuleExtensionsKt;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.DiscountOverview;
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule;
import com.mercadopago.android.px.model.internal.SummaryInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SummaryDetailDescriptorMapper extends Mapper<
    SummaryDetailDescriptorMapper.Model,
    List<AmountDescriptorView.Model>> {

    @NonNull private final AmountRepository amountRepository;
    @NonNull private final SummaryInfo summaryInfo;
    @NonNull private final AmountDescriptorViewModelFactory amountDescriptorViewModelFactory;

    public SummaryDetailDescriptorMapper(
        @NonNull final AmountRepository amountRepository,
        @NonNull final SummaryInfo summaryInfo,
        @NonNull final AmountDescriptorViewModelFactory amountDescriptorViewModelFactory) {
        this.amountRepository = amountRepository;
        this.summaryInfo = summaryInfo;
        this.amountDescriptorViewModelFactory = amountDescriptorViewModelFactory;
    }

    private void addDiscountRow(@NonNull final Model value,
        @NonNull final Collection<AmountDescriptorView.Model> list) {
        final DiscountConfigurationModel discountModel = value.discountModel;
        final AmountConfiguration amountConfiguration = value.amountConfiguration;
        final DiscountOverview discountOverview = discountModel.getDiscountOverview();
        final boolean hasSplit = amountConfiguration != null && amountConfiguration.allowSplit();

        if (discountOverview != null) {
            final AmountDescriptorView.Model model = amountDescriptorViewModelFactory.create(
                discountOverview,
                hasSplit,
                v -> value.onClickListener.onDiscountAmountDescriptorClicked(discountModel)
            );
            list.add(model);
        }
    }

    private void addChargesRow(@NonNull final Model value,
        @NonNull final Collection<AmountDescriptorView.Model> list) {
        final PaymentTypeChargeRule chargeRule = Objects.requireNonNull(value.chargeRule);
        final AmountDescriptorView.OnClickListener onClickListener = value.onClickListener;
        final AmountDescriptorView.Model model = amountDescriptorViewModelFactory.create(
            chargeRule,
            onClickListener
        );

        list.add(model);
    }

    private void addPurchaseRow(@NonNull final List<AmountDescriptorView.Model> list) {
        if (!list.isEmpty()) {
            final AmountDescriptorView.Model model =
                amountDescriptorViewModelFactory.create(summaryInfo, amountRepository.getItemsAmount());
            list.add(0, model);
        }
    }

    @Override
    public List<AmountDescriptorView.Model> map(@NonNull final Model value) {
        final List<AmountDescriptorView.Model> list = new ArrayList<>();

        addDiscountRow(value, list);
        if (value.chargeRule != null && !ChargeRuleExtensionsKt.isChargeZero(value.chargeRule)) {
            addChargesRow(value, list);
        }
        addPurchaseRow(list);

        return list;
    }

    public static class Model {
        @NonNull final DiscountConfigurationModel discountModel;
        @Nullable final PaymentTypeChargeRule chargeRule;
        @Nullable final AmountConfiguration amountConfiguration;
        @NonNull final AmountDescriptorView.OnClickListener onClickListener;

        public Model(
            @NonNull final DiscountConfigurationModel discountModel,
            @Nullable final PaymentTypeChargeRule chargeRule,
            @Nullable final AmountConfiguration amountConfiguration,
            @NonNull final AmountDescriptorView.OnClickListener onClickListener
        ) {
            this.discountModel = discountModel;
            this.chargeRule = chargeRule;
            this.amountConfiguration = amountConfiguration;
            this.onClickListener = onClickListener;
        }
    }
}