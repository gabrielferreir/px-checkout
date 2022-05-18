package com.mercadopago.android.px.internal.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.PayerCost;
import java.math.BigDecimal;

public interface AmountRepository {

    /**
     * Final amount value to pay
     * Discounts, charges and payer costs are involved on this value.
     *
     * @return amount to be processed by groups and installments
     */
    @NonNull
    BigDecimal getAmountToPay(@NonNull String paymentTypeId, @Nullable PayerCost payerCost);

    /**
     * Final amount value to pay for this specific discount model without payer cost
     *
     * @param paymentTypeId the payment type id
     * @param discountModel the discount model
     * @return amount
     */
    @NonNull
    BigDecimal getAmountToPay(@NonNull String paymentTypeId, @NonNull DiscountConfigurationModel discountModel);

    /**
     * Partial amount to pay - No charges, discounts or payer costs applied
     *
     * @return amount summation of items values * quantity
     */
    @NonNull
    BigDecimal getItemsAmount();

    /**
     * Partial amount to pay, it's needed for graphic proposes.
     *
     * @return amount items plus charges.
     */
    @NonNull
    BigDecimal getItemsPlusCharges(@NonNull String paymentTypeId);

    /**
     * Partial amount that represents charges.
     * It can be credit card extra charges, payment method charges or both.
     *
     * @return amount charges.
     */
    @NonNull
    BigDecimal getAppliedCharges(@NonNull String paymentTypeId, @Nullable PayerCost payerCost);

    /**
     * Amount to pay with taxes and no discount applied.
     *
     * @return amount without discount (items + charges + taxes).
     */
    @NonNull
    BigDecimal getAmountWithoutDiscount(@NonNull String paymentTypeId, @Nullable PayerCost payerCost);

    /**
     * Amount to pay with no tax and no discount applied.
     *
     * @return amount without tax and discount (items + charges).
     */
    @NonNull
    BigDecimal getTaxFreeAmount(@NonNull String paymentTypeId, @Nullable PayerCost payerCost);
}
