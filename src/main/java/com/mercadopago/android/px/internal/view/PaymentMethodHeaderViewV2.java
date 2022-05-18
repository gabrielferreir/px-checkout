package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.experiments.BadgeVariant;
import com.mercadopago.android.px.internal.experiments.Variant;
import com.mercadopago.android.px.internal.experiments.VariantHandler;
import com.mercadopago.android.px.model.PaymentTypes;

import java.util.List;

public class PaymentMethodHeaderViewV2 extends PaymentMethodHeaderView {

    private Listener listener;
    private View installmentsContainer;

    public PaymentMethodHeaderViewV2(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentMethodHeaderViewV2(final Context context, @Nullable final AttributeSet attrs,
        final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void inflate() {
        inflate(getContext(), R.layout.px_view_installments_header_v2, this);
        installmentsContainer = findViewById(R.id.installments_container);
    }

    @Override
    public void updateData(final boolean hasPayerCost, final boolean isDisabled, final boolean hasBehaviour) {
        this.isDisabled = isDisabled;
        this.hasBehaviour = hasBehaviour;
        setHelperVisibility(isDisabled || hasBehaviour);
        changeInstallmentsState(hasPayerCost);
        configureTitleVisibility(isDisabled, hasPayerCost);
    }

    @Override
    public void setListener(final PaymentMethodHeaderView.Listener listener) {
        this.listener = listener;
        setOnClickListener(v -> {
            if (isDisabled) {
                listener.onDisabledDescriptorViewClick();
            } else if (hasBehaviour) {
                listener.onBehaviourDescriptorViewClick();
            }
        });
    }

    @Override
    public void configureExperiment(@NonNull final List<Variant> variants) {
        for (final Variant variant : variants) {
            variant.process(new VariantHandler() {
                @Override
                public void visit(@NonNull final BadgeVariant variant) {
                    titlePager.setBadgeExperimentVariant(variant);
                }
            });
        }
    }

    @Override
    public void showInstallmentsListTitle() {
        // do nothing
    }

    @Override
    public void trackPagerPosition(final float positionOffset, final Model model) {
        // do nothing
    }

    private void changeInstallmentsState(final boolean hasPayerCost) {
        if (hasPayerCost) {
            showInstallments();
        } else {
            hideInstallments();
        }
    }

    private void hideInstallments() {
        installmentsContainer.setVisibility(View.GONE);
        titlePager.setVisibility(View.VISIBLE);
    }

    private void showInstallments() {
        installmentsContainer.setVisibility(View.VISIBLE);
        titlePager.setVisibility(View.GONE);
        listener.onInstallmentViewUpdated();
    }

    private void configureTitleVisibility(final boolean isDisabled, final boolean hasPayerCost) {
        if (paymentType.equals(PaymentTypes.DEBIT_CARD))
            setTitleVisibility(isDisabled || hasPayerCost);
        else
            setTitleVisibility(!hasPayerCost);
    }

}