package com.mercadopago.android.px.internal.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.TextUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class PaymentResultReceipt extends ConstraintLayout {

    public PaymentResultReceipt(final Context context) {
        this(context, null);
    }

    public PaymentResultReceipt(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentResultReceipt(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.px_payment_result_receipt, this);
        ((ImageView) findViewById(R.id.icon)).setImageResource(R.drawable.px_receipt);
    }

    public void setReceiptId(@NonNull final String receiptId) {
        final MPTextView description = findViewById(R.id.description);
        final MPTextView date = findViewById(R.id.date);

        final String divider = getResources().getString(R.string.px_date_divider);
        final Calendar calendar = Calendar.getInstance();
        final Locale locale = getResources().getConfiguration().locale;

        final String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        final String month = new SimpleDateFormat("MMMM", locale).format(calendar.getTime());
        final String year = String.valueOf(calendar.get(Calendar.YEAR));

        final StringBuilder builder = new StringBuilder()
            .append(day).append(" ").append(divider).append(" ").append(month).append(" ")
            .append(divider).append(" ").append(year);

        date.setText(builder.toString());
        description.setText(TextUtil.format(getContext(), R.string.px_receipt, receiptId));
    }
}