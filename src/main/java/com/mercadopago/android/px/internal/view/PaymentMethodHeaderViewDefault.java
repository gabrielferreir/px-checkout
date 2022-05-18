package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.experiments.BadgeVariant;
import com.mercadopago.android.px.internal.experiments.PulseVariant;
import com.mercadopago.android.px.internal.experiments.Variant;
import com.mercadopago.android.px.internal.experiments.VariantHandler;
import com.mercadopago.android.px.internal.view.experiments.ExperimentHelper;
import com.mercadopago.android.px.internal.viewmodel.GoingToModel;
import com.mercadopago.android.px.model.PaymentTypes;

import java.util.List;

import static com.mercadopago.android.px.internal.util.ViewUtils.hasEndedAnim;

public class PaymentMethodHeaderViewDefault extends PaymentMethodHeaderView {

    /* default */ final View titleView;

    /* default */ final Animation rotateUp;

    /* default */ final Animation rotateDown;

    private FrameLayout experimentContainer;

    private PulseView pulse;

    private ImageView arrow;

    public PaymentMethodHeaderViewDefault(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentMethodHeaderViewDefault(final Context context, @Nullable final AttributeSet attrs,
        final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rotateUp = AnimationUtils.loadAnimation(context, R.anim.px_rotate_up);
        rotateDown = AnimationUtils.loadAnimation(context, R.anim.px_rotate_down);
        titleView = findViewById(R.id.installments_title);
        experimentContainer = findViewById(R.id.pulse_experiment_container);
        titleView.setVisibility(GONE);
    }

    @Override
    protected void inflate() {
        inflate(getContext(), R.layout.px_view_installments_header, this);
    }

    @Override
    public void updateData(final boolean hasPayerCost, final boolean isDisabled, final boolean hasBehaviour) {
        super.updateData(hasPayerCost, isDisabled, hasBehaviour);
        final boolean isExpandable = hasPayerCost && !isDisabled;

        showTitlePager(hasPayerCost);
        setArrowVisibility(isExpandable);
        setHelperVisibility(isDisabled || hasBehaviour);

        setClickable(hasPayerCost || isDisabled || hasBehaviour);
    }

    @Override
    public void setListener(final PaymentMethodHeaderView.Listener listener) {
        setOnClickListener(v -> {
            if (isDisabled) {
                listener.onDisabledDescriptorViewClick();
            } else if (hasBehaviour) {
                listener.onBehaviourDescriptorViewClick();
            } else if (hasEndedAnim(arrow)) {
                if (titleView.getVisibility() == VISIBLE) {
                    arrow.startAnimation(rotateDown);
                    listener.onInstallmentsSelectorCancelClicked();
                } else {
                    arrow.startAnimation(rotateUp);
                    listener.onDescriptorViewClicked();
                    if (pulse != null) {
                        pulse.stopRippleAnimation();
                    }
                }
            }
        });
    }

    @Override
    public void configureExperiment(@NonNull final List<Variant> variants) {
        for (final Variant variant : variants) {
            variant.process(new VariantHandler() {
                @Override
                public void visit(@NonNull final PulseVariant variant) {
                    configurePulseExperiment(variant);
                }

                @Override
                public void visit(@NonNull final BadgeVariant variant) {
                    configureBadgeExperiment(variant);
                }
            });
        }
    }

    private void configureBadgeExperiment(@NonNull final BadgeVariant variant) {
        titlePager.setBadgeExperimentVariant(variant);
    }

    private void configurePulseExperiment(@NonNull final PulseVariant variant) {
        if (experimentContainer.getChildCount() == 0) {
            ExperimentHelper.INSTANCE.applyExperimentViewBy(experimentContainer, variant);
        }

        pulse = experimentContainer.findViewById(R.id.pulse);
        if (pulse != null) {
            pulse.startRippleAnimation();
        }
        arrow = experimentContainer.findViewById(R.id.arrow);
    }

    @Override
    public void showInstallmentsListTitle() {
        titleView.setVisibility(VISIBLE);
        titlePager.setVisibility(GONE);
    }

    private void showTitlePager(final boolean isClickable) {
        if (titleView.getVisibility() == VISIBLE) {
            arrow.startAnimation(rotateDown);
        }
        if (paymentType.equals(PaymentTypes.DEBIT_CARD)) {
            setTitleVisibility(isDisabled || splitSelection);
        } else {
            setTitleVisibility(true);
        }
        titleView.setVisibility(GONE);

        setClickable(isClickable);
    }

    @Override
    public void trackPagerPosition(float positionOffset, final Model model) {
        if (model.goingTo == GoingToModel.BACKWARDS) {
            positionOffset = 1.0f - positionOffset;
        }

        if (model.currentIsExpandable) {
            if (model.nextIsExpandable) {
                experimentContainer.setAlpha(1.0f);
            } else {
                experimentContainer.setAlpha(1.0f - positionOffset);
            }
        } else {
            if (model.nextIsExpandable) {
                experimentContainer.setAlpha(positionOffset);
            } else {
                experimentContainer.setAlpha(0.0f);
            }
        }
    }

    private void setArrowVisibility(final boolean visible) {
        experimentContainer.setAlpha(visible ? 1.0f : 0.0f);
    }
}