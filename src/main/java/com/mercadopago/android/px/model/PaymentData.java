package com.mercadopago.android.px.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.util.TextUtil;
import java.io.Serializable;
import java.math.BigDecimal;

public final class PaymentData implements Serializable {

    /**
     * Raw amount, contains preference total value or split value for this payment method (not including taxes).
     * Always available.
     */
    private BigDecimal rawAmount;

    /**
     * No discount amount, contains preference total value or split value for this payment method (including taxes)
     * with no discount applied.
     */
    private BigDecimal noDiscountAmount;

    /**
     * Always available.
     */
    private BigDecimal transactionAmount;

    /**
     * Always available.
     */
    private PaymentMethod paymentMethod;

    /**
     * Always available.
     */
    private Payer payer;

    /**
     * Available if it's guessing card flow.
     */
    @Nullable private Issuer issuer;
    /**
     * Available if it's guessing or saved card flow.
     */
    @Nullable private PayerCost payerCost;

    /**
     * Available if it's guessing or saved card flow.
     */
    @Nullable private Token token;

    @Nullable private TransactionInfo transactionInfo;

    /**
     * @deprecated CouponCode discount is not supported anymore.
     */
    @Deprecated
    private String couponCode;

    @Nullable private Discount discount;

    @Nullable private Campaign campaign;

    /**
     * @deprecated you should't be allowed to instantiate this class. PX checkout is responsible for instantiate and
     * modify this class.
     */
    @Deprecated
    public PaymentData() {
        // do nothing
    }

    @Nullable
    public Campaign getCampaign() {
        return campaign;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * @deprecated you should't be allowed to instantiate this class. PX checkout is responsible for instantiate and
     * modify this class.
     */
    @Deprecated
    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Nullable
    public Issuer getIssuer() {
        return issuer;
    }

    /**
     * @deprecated you should't be allowed to instantiate this class. PX checkout is responsible for instantiate and
     * modify this class.
     */
    @Deprecated
    public void setIssuer(@Nullable final Issuer issuer) {
        this.issuer = issuer;
    }

    @Nullable
    public PayerCost getPayerCost() {
        return payerCost;
    }

    /**
     * @deprecated you should't be allowed to instantiate this class. PX checkout is responsible for instantiate and
     * modify this class.
     */
    @Deprecated
    public void setPayerCost(@Nullable final PayerCost payerCost) {
        this.payerCost = payerCost;
    }

    @Nullable
    public Token getToken() {
        return token;
    }

    /**
     * @deprecated you should't be allowed to instantiate this class. PX checkout is responsible for instantiate and
     * modify this class.
     */
    @Deprecated
    public void setToken(@Nullable final Token token) {
        this.token = token;
    }

    /**
     * @deprecated you should't be allowed to instantiate this class. PX checkout is responsible for instantiate and
     * modify this class.
     */
    @Deprecated
    public void setDiscount(@Nullable final Discount discount) {
        this.discount = discount;
    }

    @Nullable
    public Discount getDiscount() {
        return discount;
    }

    public Payer getPayer() {
        return payer;
    }

    /**
     * @deprecated you should't be allowed to instantiate this class. PX checkout is responsible for instantiate and
     * modify this class.
     */
    @Deprecated
    public void setPayer(final Payer payer) {
        this.payer = payer;
    }

    /**
     * @param couponCode The coupon code
     * @deprecated CouponCode discount is not supported anymore.
     */
    @Deprecated
    public void setCouponCode(final String couponCode) {
        this.couponCode = couponCode;
    }

    /**
     * @return code
     * @deprecated CouponCode discount is not supported anymore.
     */
    @Deprecated
    public String getCouponCode() {
        return couponCode;
    }

    /**
     * @deprecated will not longer be valid, use {{@link #getRawAmount()}}
     */
    @Deprecated
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * Raw amount, contains preference total value or split value for this payment method (not including taxes).
     *
     * @return raw amount to pay (not including taxes)
     */
    public BigDecimal getRawAmount() {
        return rawAmount;
    }

    /**
     * No discount amount, contains preference total value or split value for this payment method (including taxes)
     * and no discount.
     * @return no discount amount (including taxes)
     */
    public BigDecimal getNoDiscountAmount() {
        return noDiscountAmount;
    }

    /**
     * @param transactionAmount will not longer be valid.
     * @deprecated will not longer be valid, use {{@link #getRawAmount()}}
     */
    @Deprecated
    public void setTransactionAmount(final BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    @Deprecated
    public void setCampaign(@Nullable final Campaign campaign) {
        this.campaign = campaign;
    }

    public boolean containsCardInfo() {
        return getToken() != null && !TextUtil.isEmpty(getToken().getCardId());
    }

    /**
     * @deprecated you should't be allowed to instantiate this class. PX checkout is responsible for instantiate and
     * modify this class.
     */
    @Deprecated
    public void setRawAmount(final BigDecimal rawAmount) {
        this.rawAmount = rawAmount;
    }

    /**
     * TransactionInfo, contains bank info for this payment method.
     * @return TransactionInfo
     */
    @Nullable
    public TransactionInfo getTransactionInfo() {
        return transactionInfo;
    }

    public static final class Builder {

        /* default */ BigDecimal transactionAmount;
        /* default */ PaymentMethod paymentMethod;
        /* default */ Payer payer;
        /* default */ BigDecimal rawAmount;
        /* default */ BigDecimal noDiscountAmount;

        @Nullable /* default */ Discount discount;
        @Nullable /* default */ Campaign campaign;
        @Nullable /* default */ Issuer issuer;
        @Nullable /* default */ PayerCost payerCost;
        @Nullable /* default */ Token token;
        @Nullable /* default */ TransactionInfo transactionIfo;

        @Deprecated
        public Builder setTransactionAmount(final BigDecimal transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }

        public Builder setPaymentMethod(final PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder setTransactionInfo(final TransactionInfo transactionIfo) {
            this.transactionIfo = transactionIfo;
            return this;
        }

        public Builder setPayer(final Payer payer) {
            this.payer = payer;
            return this;
        }

        public Builder setRawAmount(final BigDecimal rawAmount) {
            this.rawAmount = rawAmount;
            return this;
        }

        public Builder setNoDiscountAmount(final BigDecimal noDiscountAmount) {
            this.noDiscountAmount = noDiscountAmount;
            return this;
        }

        public Builder setDiscount(@Nullable final Discount discount) {
            this.discount = discount;
            return this;
        }

        public Builder setCampaign(@Nullable final Campaign campaign) {
            this.campaign = campaign;
            return this;
        }

        public Builder setIssuer(@Nullable final Issuer issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder setPayerCost(@Nullable final PayerCost payerCost) {
            this.payerCost = payerCost;
            return this;
        }

        public Builder setToken(@Nullable final Token token) {
            this.token = token;
            return this;
        }

        public PaymentData createPaymentData() {
            return create(this);
        }
    }

    /* default */
    static PaymentData create(@NonNull final Builder paymentDataBuilder) {
        final PaymentData paymentData = new PaymentData();
        paymentData.rawAmount = paymentDataBuilder.rawAmount;
        paymentData.noDiscountAmount = paymentDataBuilder.noDiscountAmount;
        paymentData.campaign = paymentDataBuilder.campaign;
        paymentData.discount = paymentDataBuilder.discount;
        paymentData.issuer = paymentDataBuilder.issuer;
        paymentData.payerCost = paymentDataBuilder.payerCost;
        paymentData.token = paymentDataBuilder.token;
        paymentData.payer = paymentDataBuilder.payer;
        paymentData.transactionAmount = paymentDataBuilder.transactionAmount;
        paymentData.paymentMethod = paymentDataBuilder.paymentMethod;
        paymentData.transactionInfo = paymentDataBuilder.transactionIfo;
        return paymentData;
    }
}
