package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.textformatter.AmountLabeledFormatter;
import com.mercadopago.android.px.internal.util.textformatter.TextFormatter;
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorView;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.Split;

/**
 * Model used to instantiate PaymentMethodDescriptorView for payment methods with payer costs. This model is used for
 * debit_card
 */
public final class DebitCardDescriptorModel extends PaymentMethodDescriptorView.Model {

    private final Currency currency;
    private final AmountConfiguration amountConfiguration;

    @NonNull
    public static PaymentMethodDescriptorView.Model createFrom(@NonNull final Currency currency,
        @NonNull final AmountConfiguration amountConfiguration) {
        return new DebitCardDescriptorModel(currency, amountConfiguration);
    }

    private DebitCardDescriptorModel(@NonNull final Currency currency,
        @NonNull final AmountConfiguration amountConfiguration) {
        this.currency = currency;
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
    }

    private void updateInstallment(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final Context context,
        @NonNull final TextView textView) {

        final Spannable amount = TextFormatter.withCurrency(currency)
            .withSpace()
            .amount(getCurrentPayerCost().getInstallmentAmount())
            .normalDecimals()
            .into(textView)
            .toSpannable();

        new AmountLabeledFormatter(spannableStringBuilder, context)
            .withTextColor(ContextCompat.getColor(context, R.color.px_expressCheckoutTextColor))
            .withSemiBoldStyle()
            .apply(amount);
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
        builder.append(getCurrentPayerCost().getInstallmentAmount().toString())
            .append(TextUtil.SPACE)
            .append(context.getResources().getString(R.string.px_money));

        return builder.toString();
    }
}