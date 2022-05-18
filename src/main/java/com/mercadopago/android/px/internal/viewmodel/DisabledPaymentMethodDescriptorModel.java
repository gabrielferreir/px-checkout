package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.textformatter.SpannableFormatter;
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorView;
import com.mercadopago.android.px.model.internal.Text;

public class DisabledPaymentMethodDescriptorModel extends PaymentMethodDescriptorView.Model {

    @Nullable public Text message;
    @NonNull private String description = TextUtil.EMPTY;

    @NonNull
    public static PaymentMethodDescriptorView.Model createFrom(@NonNull final Text message) {
        return new DisabledPaymentMethodDescriptorModel(message);
    }

    /* default */ DisabledPaymentMethodDescriptorModel(@Nullable final Text message) {
        this.message = message;
    }

    @Override
    public void updateLeftSpannable(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final TextView textView) {
        final Context context = textView.getContext();
        final SpannableFormatter amountLabeledFormatter = new SpannableFormatter(spannableStringBuilder, context);
        amountLabeledFormatter.withTextColor(ContextCompat.getColor(context, R.color.px_expressCheckoutTextColor))
            .withStyle(PxFont.REGULAR);
        if (message != null && TextUtil.isNotEmpty(message.getMessage())) {
            description = message.getMessage();
        } else {
            description = context.getString(R.string.px_payment_method_disable_title);
        }

        amountLabeledFormatter.apply(description);
    }

    @Override
    protected String getAccessibilityContentDescription(@NonNull final Context context) {
        return description;
    }
}