package com.mercadopago.android.px.internal.util.textformatter;

import android.text.Spannable;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.model.Currency;

public class TextFormatter {

    @NonNull private final TextView textView;
    @NonNull private final Style style;

    TextFormatter(@NonNull final TextView textView, @NonNull final Style style) {
        this.textView = textView;
        this.style = style;
        setFormatted();
    }

    public static CurrencyFormatter withCurrency(@NonNull final Currency currency) {
        return new CurrencyFormatter(currency);
    }

    private void setFormatted() {
        textView.setText(style.apply(R.string.px_string_holder, textView.getContext()));
    }

    public TextFormatter visible(final boolean visible) {
        textView.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public Spannable toSpannable() {
        return style.apply(null);
    }
}
