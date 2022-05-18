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
import com.mercadopago.android.px.model.AccountMoneyMetadata;
import com.mercadopago.android.px.model.Currency;
import java.math.BigDecimal;

public class AccountMoneyDescriptorModel extends PaymentMethodDescriptorView.Model {

    private final AccountMoneyMetadata accountMoneyMetadata;
    private final Currency currency;
    private final BigDecimal amountToPay;
    private String sliderTitle = TextUtil.EMPTY;
    private boolean showAmount = false;

    @NonNull
    public static PaymentMethodDescriptorView.Model createFrom(@NonNull final AccountMoneyMetadata accountMoneyMetadata,
        @NonNull final Currency currency, @NonNull final BigDecimal amountToPay) {
        return new AccountMoneyDescriptorModel(accountMoneyMetadata, currency, amountToPay);
    }

    /* default */ AccountMoneyDescriptorModel(@NonNull final AccountMoneyMetadata accountMoneyMetadata,
        @NonNull final Currency currency, @NonNull final BigDecimal amountToPay) {
        this.accountMoneyMetadata = accountMoneyMetadata;
        this.currency = currency;
        this.amountToPay = amountToPay;
    }

    @Override
    public void updateLeftSpannable(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final TextView textView) {

        final Context context = textView.getContext();

        if (showAmount) {
            updateInstallment(spannableStringBuilder, context, textView);
            spannableStringBuilder.append(TextUtil.SPACE);
        }

        if (accountMoneyMetadata.displayInfo != null) {
            sliderTitle = accountMoneyMetadata.displayInfo.getSliderTitle();
            if (TextUtil.isEmpty(sliderTitle)) {
                spannableStringBuilder.append(TextUtil.SPACE);
            } else {
                final AmountLabeledFormatter amountLabeledFormatter =
                    new AmountLabeledFormatter(spannableStringBuilder, context)
                        .withTextColor(ContextCompat.getColor(context, R.color.px_expressCheckoutTextColor));
                amountLabeledFormatter.apply(sliderTitle);
            }
        }
    }

    private void updateInstallment(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final Context context,
        @NonNull final TextView textView) {

        final Spannable amount = TextFormatter.withCurrency(currency)
            .amount(amountToPay)
            .normalDecimals()
            .into(textView)
            .toSpannable();

        new AmountLabeledFormatter(spannableStringBuilder, context)
            .withTextColor(ContextCompat.getColor(context, R.color.px_expressCheckoutTextColor))
            .withSemiBoldStyle()
            .apply(amount);
    }

    @Override
    public void formatForRemedy() {
        super.formatForRemedy();
        showAmount = true;
    }

    @Override
    protected String getAccessibilityContentDescription(@NonNull final Context context) {
        return sliderTitle;
    }
}
