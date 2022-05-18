package com.mercadopago.android.px.internal.view;

import android.content.Context;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.presentation.extensions.ViewExtKt;
import com.mercadopago.android.px.internal.experiments.Variant;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.experiments.ExperimentHelper;
import com.mercadopago.android.px.model.PayerCost;

public class PaymentMethodDescriptorView extends LinearLayout {

    final MPTextView leftText;
    final FrameLayout experimentContainer;
    MPTextView rightText;

    public PaymentMethodDescriptorView(final Context context) {
        this(context, null);
    }

    public PaymentMethodDescriptorView(final Context context,
        @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentMethodDescriptorView(final Context context, @Nullable final AttributeSet attrs,
        final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.px_view_payment_method_descriptor, this);
        leftText = findViewById(R.id.left_text);
        experimentContainer = findViewById(R.id.badge_experiment_container);
        leftText.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        experimentContainer.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    public void update(@NonNull final Model model) {
        final SpannableStringBuilder leftSpannableBuilder = new SpannableStringBuilder();
        model.updateLeftSpannable(leftSpannableBuilder, leftText);
        leftText.setText(leftSpannableBuilder);
        final SpannableStringBuilder rightSpannableBuilder = new SpannableStringBuilder();
        model.updateRightSpannable(rightSpannableBuilder, leftText);
        rightText.setText(rightSpannableBuilder);
        setContentDescription(TextUtil.EMPTY);
        model.updateDrawableBackground(rightText);
    }

    public void configureExperiment(Variant variant) {
        if (experimentContainer.getChildCount() == 0) {
            ExperimentHelper.INSTANCE.applyExperimentViewBy(experimentContainer, variant);
        }
        rightText = experimentContainer.findViewById(R.id.right_text);
    }

    public void updateContentDescription(@NonNull final Model model) {
        setContentDescription(model.getAccessibilityContentDescription(getContext()));
    }

    public abstract static class Model {
        protected int payerCostSelected = PayerCost.NO_SELECTED;
        protected boolean userWantToSplit = true;
        protected boolean hasBehaviour = false;

        public abstract void updateLeftSpannable(@NonNull final SpannableStringBuilder spannableStringBuilder,
            @NonNull final TextView textView);

        public void updateRightSpannable(@NonNull final SpannableStringBuilder spannableStringBuilder,
            @NonNull final TextView textView) {
        }

        public final void setCurrentPayerCost(final int payerCostSelected) {
            this.payerCostSelected = payerCostSelected;
        }

        @CallSuper
        public void updateDrawableBackground(@NonNull final TextView textView) {
            ViewExtKt.resetDrawableBackgroundColor(textView);
        }

        public int getCurrentInstalment() {
          return PayerCost.NO_SELECTED;
        }

        public final void setSplit(final boolean split) {
            userWantToSplit = split;
        }

        public final void setHasBehaviour(final boolean hasBehaviour) {
            this.hasBehaviour = hasBehaviour;
        }

        public boolean hasPayerCostList() {
            return false;
        }

        public boolean hasBehaviour() {
            return hasBehaviour;
        }

        protected String getAccessibilityContentDescription(@NonNull final Context context) {
            return TextUtil.EMPTY;
        }

        @CallSuper
        public void formatForRemedy() {
            setSplit(false);
        }
    }
}