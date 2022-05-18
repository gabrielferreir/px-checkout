package com.mercadopago.android.px.internal.util.textformatter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import android.text.Spannable;
import android.text.SpannableString;
import com.mercadopago.android.px.internal.util.TextUtil;

class StyleNormalDecimal extends Style {

    StyleNormalDecimal(@NonNull final AmountFormatter amountFormatter) {
        super(amountFormatter);
    }

    @Override
    public Spannable apply(@StringRes final int holder, final Context context) {
        return new SpannableString(TextUtil.format(context, holder, apply(null)));
    }

    @Override
    public Spannable apply(final CharSequence charSequence) {
        return amountFormatter.apply(charSequence);
    }

    @Override
    public Spannable toSpannable() {
        return apply(null);
    }
}
