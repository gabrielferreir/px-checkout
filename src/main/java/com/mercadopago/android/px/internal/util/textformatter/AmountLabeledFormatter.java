package com.mercadopago.android.px.internal.util.textformatter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.presentation.extensions.SpannableExtKt;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import java.util.Locale;

public class AmountLabeledFormatter extends ChainFormatter {

    private int installment;
    private int textColor;
    private final Context context;
    private final SpannableStringBuilder spannableStringBuilder;
    private boolean semiBoldStyle;

    public AmountLabeledFormatter(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final Context context) {
        this.spannableStringBuilder = spannableStringBuilder;
        this.context = context;
        semiBoldStyle = false;
    }

    public AmountLabeledFormatter withInstallment(final int installment) {
        this.installment = installment;
        return this;
    }

    public AmountLabeledFormatter withTextColor(final int color) {
        textColor = color;
        return this;
    }

    public AmountLabeledFormatter withSemiBoldStyle() {
        semiBoldStyle = true;
        return this;
    }

    @Override
    public Spannable apply(@NonNull final CharSequence amount) {
        final int indexStart = spannableStringBuilder.length();
        final int length;
        if (installment != 0) {
            final int holder = R.string.px_amount_with_installments_holder;
            final String installmentAmount = String.format(Locale.getDefault(), "%d", installment);
            final int holderFixedCharactersLength = 2;
            length = installmentAmount.length() + holderFixedCharactersLength + amount.length();
            final CharSequence charSequence = TextUtil.format(context, holder, installmentAmount, amount);
            spannableStringBuilder.append(charSequence);
        } else {
            final int holder = R.string.px_string_holder;
            final CharSequence charSequence = TextUtil.format(context, holder, amount);
            spannableStringBuilder.append(charSequence);
            length = charSequence.length();
        }

        SpannableExtKt.setColor(spannableStringBuilder, textColor, indexStart, indexStart + length);
        updateTextStyle(indexStart, indexStart + length);

        return spannableStringBuilder;
    }

    private void updateTextStyle(final int indexStart, final int indexEnd) {
        ViewUtils.setFontInSpannable(context, semiBoldStyle ? PxFont.SEMI_BOLD : PxFont.REGULAR, spannableStringBuilder,
            indexStart, indexEnd);
    }
}