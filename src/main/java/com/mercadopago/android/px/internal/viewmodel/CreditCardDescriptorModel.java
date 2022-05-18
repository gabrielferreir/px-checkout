package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.presentation.extensions.ViewExtKt;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.util.textformatter.AmountLabeledFormatter;
import com.mercadopago.android.px.internal.util.textformatter.InterestFormatter;
import com.mercadopago.android.px.internal.util.textformatter.PayerCostFormatter;
import com.mercadopago.android.px.internal.util.textformatter.SpannableFormatter;
import com.mercadopago.android.px.internal.util.textformatter.TextFormatter;
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorView;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.InterestFree;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.Split;
import com.mercadopago.android.px.model.internal.Text;
import java.math.BigDecimal;

/**
 * Model used to instantiate PaymentMethodDescriptorView for payment methods with payer costs. This model is used for
 * credit_card
 */
public final class CreditCardDescriptorModel extends PaymentMethodDescriptorView.Model {

    private final Currency currency;
    private final InterestFree interestFree;
    private final AmountConfiguration amountConfiguration;
    private final Text installmentsRightHeader;

    @NonNull
    public static PaymentMethodDescriptorView.Model createFrom(@NonNull final Currency currency,
        @Nullable final Text installmentsRightHeader, @Nullable final InterestFree interestFree,
        @NonNull final AmountConfiguration amountConfiguration) {
        return new CreditCardDescriptorModel(currency, installmentsRightHeader, interestFree, amountConfiguration);
    }

    @Override
    public boolean hasPayerCostList() {
        return amountConfiguration.getAppliedPayerCost(userWantToSplit).size() > 1;
    }

    private CreditCardDescriptorModel(@NonNull final Currency currency, @Nullable final Text installmentsRightHeader,
        @Nullable final InterestFree interestFree, @NonNull final AmountConfiguration amountConfiguration) {
        this.currency = currency;
        this.installmentsRightHeader = installmentsRightHeader;
        this.interestFree = interestFree;
        this.amountConfiguration = amountConfiguration;
        final Split splitConfig = amountConfiguration.getSplitConfiguration();
        if (splitConfig != null) {
            userWantToSplit = splitConfig.defaultEnabled;
        }
    }

    @Override
    public void updateLeftSpannable(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final TextView textView) {
        final Context context = textView.getContext();
        updateInstallment(spannableStringBuilder, context, textView);
        updateTotalAmountDescriptionSpannable(spannableStringBuilder, context);
        updateInterestDescriptionSpannable(spannableStringBuilder, context);
        updateInterestRate(spannableStringBuilder, context);
    }

    @Override
    public void updateRightSpannable(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final TextView textView) {
        super.updateRightSpannable(spannableStringBuilder, textView);
        updateInstallmentsInfo(spannableStringBuilder, textView.getContext());
    }

    private void updateInterestRate(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final Context context) {
        final Text interestRate;
        if ((interestRate = getCurrentPayerCost().getInterestRate()) != null) {
            new InterestFormatter(spannableStringBuilder).withText(interestRate).apply(context);
        }
    }

    @Override
    public void updateDrawableBackground(@NonNull final TextView textView) {
        super.updateDrawableBackground(textView);
        if (payerCostSelected == PayerCost.NO_SELECTED && installmentsRightHeader != null) {
            ViewExtKt.setDrawableBackgroundColor(textView, installmentsRightHeader.getBackgroundColor());
        }
    }

    private void updateInstallmentsInfo(@NonNull final SpannableStringBuilder spannableStringBuilder,
        final Context context) {
        if (payerCostSelected != PayerCost.NO_SELECTED) {
            return;
        }
        new SpannableFormatter(spannableStringBuilder, context)
            .apply(installmentsRightHeader);
    }

    private void updateInstallment(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final Context context,
        @NonNull final TextView textView) {

        final Spannable amount = TextFormatter.withCurrency(currency)
            .amount(getCurrentPayerCost().getInstallmentAmount())
            .normalDecimals()
            .into(textView)
            .toSpannable();

        new AmountLabeledFormatter(spannableStringBuilder, context)
            .withInstallment(getCurrentPayerCost().getInstallments())
            .withTextColor(ContextCompat.getColor(context, R.color.px_expressCheckoutTextColor))
            .withSemiBoldStyle()
            .apply(amount);
    }

    /**
     * Updates total amount the user will pay with credit card, only if there are interests involved.
     */
    private void updateTotalAmountDescriptionSpannable(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final Context context) {
        if (hasAmountDescriptor()) {
            new PayerCostFormatter(spannableStringBuilder, context, getCurrentPayerCost(), currency)
                .withTextColor(ContextCompat.getColor(context, R.color.px_checkout_secondary_color))
                .apply();
        }
    }

    private boolean hasAmountDescriptor() {
        return BigDecimal.ZERO.compareTo(getCurrentPayerCost().getInstallmentRate()) < 0;
    }

    private boolean hasInterestFree() {
        return interestFree != null && interestFree.hasAppliedInstallment(getCurrentPayerCost().getInstallments());
    }

    private void updateInterestDescriptionSpannable(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final Context context) {
        if (hasInterestFree()) {
            new SpannableFormatter(spannableStringBuilder, context);
        }
    }

    @NonNull
    private PayerCost getCurrentPayerCost() {
        return amountConfiguration.getCurrentPayerCost(userWantToSplit, payerCostSelected);
    }

    @Override
    public int getCurrentInstalment() {
        return getCurrentPayerCost().getInstallments();
    }

    @Override
    protected String getAccessibilityContentDescription(@NonNull final Context context) {
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        final PayerCost currentInstallment = getCurrentPayerCost();
        final String money = context.getResources().getString(R.string.px_money);
        builder
            .append(currentInstallment.getInstallments().toString())
            .append(TextUtil.SPACE)
            .append(context.getResources().getString(R.string.px_date_divider))
            .append(TextUtil.SPACE)
            .append(currentInstallment.getInstallmentAmount().toString())
            .append(TextUtil.SPACE)
            .append(money)
            .append(TextUtil.SPACE)
            .append(hasAmountDescriptor() ? currentInstallment.getTotalAmount().floatValue() + money : TextUtil.EMPTY)
            .append(hasInterestFree() ? interestFree.getInstallmentRow().getMessage() : TextUtil.EMPTY);

        updateInterestRate(builder, context);
        updateInstallmentsInfo(builder, context);

        return builder.toString();
    }
}