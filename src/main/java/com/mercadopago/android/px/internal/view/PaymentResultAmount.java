package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import com.google.android.flexbox.FlexboxLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ViewUtils;
import java.math.BigDecimal;
import java.util.Locale;

public class PaymentResultAmount extends FlexboxLayout {

    private final MPTextView title;
    private final MPTextView detail;
    private final MPTextView noRate;
    private final MPTextView rawAmount;
    private final MPTextView discount;

    public PaymentResultAmount(final Context context) {
        this(context, null);
    }

    public PaymentResultAmount(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentResultAmount(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.px_payment_result_amount, this);
        title = findViewById(R.id.title);
        detail = findViewById(R.id.detail);
        noRate = findViewById(R.id.no_rate);
        rawAmount = findViewById(R.id.raw_amount);
        discount = findViewById(R.id.discount);
    }

    public void setModel(@NonNull final Model model) {
        title.setText(getAmountTitle(model));
        ViewUtils.loadOrGone(getAmountDetail(model), detail);
        ViewUtils.loadOrGone(getNoRate(model), noRate);

        final String discountName = model.discountName;
        if (discountName != null && !discountName.isEmpty()) {
            rawAmount.setText(model.rawAmount);
            rawAmount.setPaintFlags(rawAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            discount.setText(discountName);
        } else {
            rawAmount.setVisibility(GONE);
            discount.setVisibility(GONE);
        }
    }

    @Nullable
    private String getAmountDetail(@NonNull final Model model) {
        if (hasPayerCostWithMultipleInstallments(model.numberOfInstallments)) {
            return String.format(Locale.getDefault(), "(%s)",
                model.installmentsTotalAmount);
        }
        return null;
    }

    @NonNull
    private String getAmountTitle(@NonNull final Model model) {
        if (hasPayerCostWithMultipleInstallments(model.numberOfInstallments) && model.installmentsAmount != null &&
            !model.installmentsAmount.isEmpty()) {
            return String.format(Locale.getDefault(), "%dx %s", model.numberOfInstallments,
                model.installmentsAmount);
        } else {
            return model.amountPaid;
        }
    }

    @Nullable
    private String getNoRate(@Nullable final Model model) {
        if (hasPayerCostWithMultipleInstallments(model.numberOfInstallments)) {
            if (BigDecimal.ZERO.equals(model.installmentsRate)) {
                return getResources().getString(R.string.px_zero_rate).toLowerCase();
            }
        }
        return null;
    }

    private boolean hasPayerCostWithMultipleInstallments(@Nullable final Integer numberOfInstallments) {
        return numberOfInstallments != null && numberOfInstallments > 1;
    }

    public static final class Model {
        @NonNull public final String rawAmount;
        @NonNull public final String amountPaid;
        @Nullable public final String discountName;
        @Nullable public final Integer numberOfInstallments;
        @Nullable public final String installmentsAmount;
        @Nullable public final BigDecimal installmentsRate;
        @Nullable public final String installmentsTotalAmount;

        /* default */ Model(@NonNull final Builder builder) {
            rawAmount = builder.rawAmount;
            amountPaid = builder.amountPaid;
            discountName = builder.discountName;
            numberOfInstallments = builder.numberOfInstallments;
            installmentsAmount = builder.installmentsAmount;
            installmentsRate = builder.installmentsRate;
            installmentsTotalAmount = builder.installmentsTotalAmount;
        }

        public static class Builder {
            /* default */ String rawAmount;
            /* default */ String amountPaid;
            @Nullable /* default */ String discountName;
            @Nullable /* default */ Integer numberOfInstallments;
            @Nullable /* default */ String installmentsAmount;
            @Nullable /* default */ BigDecimal installmentsRate;
            @Nullable /* default */ String installmentsTotalAmount;

            public Builder(@NonNull final String amountPaid, @NonNull final String rawAmount) {
                this.amountPaid = amountPaid;
                this.rawAmount = rawAmount;
            }

            public Builder setNumberOfInstallments(@Nullable final Integer numberOfInstallments) {
                this.numberOfInstallments = numberOfInstallments;
                return this;
            }

            public Builder setInstallmentsAmount(@Nullable final String installmentsAmount) {
                this.installmentsAmount = installmentsAmount;
                return this;
            }

            public Builder setInstallmentsTotalAmount(@Nullable final String installmentsTotalAmount) {
                this.installmentsTotalAmount = installmentsTotalAmount;
                return this;
            }

            public Builder setInstallmentsRate(@Nullable final BigDecimal installmentsRate) {
                this.installmentsRate = installmentsRate;
                return this;
            }

            public Builder setDiscountName(@Nullable final String discountName) {
                this.discountName = discountName;
                return this;
            }

            public Model build() {
                return new Model(this);
            }
        }
    }
}