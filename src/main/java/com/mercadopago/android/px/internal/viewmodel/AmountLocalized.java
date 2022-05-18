package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.textformatter.TextFormatter;
import com.mercadopago.android.px.model.Currency;
import java.math.BigDecimal;

public class AmountLocalized implements ILocalizedCharSequence {

    @NonNull private final BigDecimal amount;
    @NonNull private final Currency currency;

    public AmountLocalized(@NonNull final BigDecimal amount, @NonNull final Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    @NonNull
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public CharSequence get(@NonNull final Context context) {
        return TextFormatter.withCurrency(currency)
            .withSpace().amount(amount)
            .normalDecimals()
            .toSpannable();
    }
}
