package com.mercadopago.android.px.internal.util.textformatter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.presentation.extensions.SpannableExtKt;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.PayerCost;

public class PayerCostFormatter {

    private PayerCost payerCost;
    private Currency currency;
    private int textColor;
    private final Context context;
    private final SpannableStringBuilder spannableStringBuilder;

    public PayerCostFormatter(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final Context context,
        @NonNull final PayerCost payerCost,
        @NonNull final Currency currency) {

        this.spannableStringBuilder = spannableStringBuilder;
        this.context = context;
        this.payerCost = payerCost;
        this.currency = currency;
    }

    public PayerCostFormatter withTextColor(final int color) {
        textColor = color;
        return this;
    }

    public Spannable apply() {
        Spannable totalAmount = new SpannableStringBuilder();
        if (payerCost.getInstallments() > 1) {
            totalAmount = TextFormatter.withCurrency(currency)
                .amount(payerCost.getTotalAmount())
                .normalDecimals()
                .apply(R.string.px_total_amount_holder, context);
        }

        final int initialIndex = spannableStringBuilder.length();
        final String separator = " ";
        spannableStringBuilder.append(separator).append(totalAmount);
        final int textLength = separator.length() + totalAmount.length();
        final int endIndex = initialIndex + textLength;

        SpannableExtKt.setColor(spannableStringBuilder, textColor, initialIndex, endIndex);
        ViewUtils.setFontInSpannable(context, PxFont.REGULAR, spannableStringBuilder, initialIndex, endIndex);

        return spannableStringBuilder;
    }
}