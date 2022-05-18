package com.mercadopago.android.px.internal.util;

import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.BenefitsMetadata;
import com.mercadopago.android.px.model.internal.Text;

public final class BenefitsHelper {

    private BenefitsHelper() {
    }

    @Nullable
    public static Text getInterestFreeText(@Nullable final BenefitsMetadata benefits, final int installments) {
        if (benefits != null && benefits.getInterestFree() != null &&
            benefits.getInterestFree().hasAppliedInstallment(installments)) {
            return benefits.getInterestFree().getInstallmentRow();
        } else {
            return null;
        }
    }

    @Nullable
    public static Text getReimbursementText(@Nullable final BenefitsMetadata benefits, final int installments) {
        if (benefits != null && benefits.getReimbursement() != null &&
            benefits.getReimbursement().hasAppliedInstallment(installments)) {
            return benefits.getReimbursement().getInstallmentRow();
        } else {
            return null;
        }
    }
}