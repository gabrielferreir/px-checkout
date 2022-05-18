package com.mercadopago.android.px.internal.util.textformatter;

import androidx.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import com.mercadopago.android.px.model.Currency;
import java.math.BigDecimal;
import java.util.Locale;

public class CurrencyFormatter extends ChainFormatter {

    final Currency currency;
    private boolean hasSpace;

    CurrencyFormatter(final Currency currency) {
        this.currency = currency;
    }

    public AmountFormatter amount(@NonNull final BigDecimal amount) {
        return new AmountFormatter(amount, this);
    }

    @Override
    public Spannable apply(final CharSequence charSequence) {
        final String space = hasSpace ? " " : "";
        return new SpannableString(
            String.format(Locale.getDefault(), "%s%s%s", currency.getSymbol(), space, charSequence));
    }

    public CurrencyFormatter withSpace() {
        hasSpace = true;
        return this;
    }
}
