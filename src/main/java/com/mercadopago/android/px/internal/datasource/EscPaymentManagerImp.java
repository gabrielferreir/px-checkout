package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.EscDeleteReason;
import com.mercadopago.android.px.internal.repository.EscPaymentManager;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.EscUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

public class EscPaymentManagerImp implements EscPaymentManager {

    @NonNull private final ESCManagerBehaviour escManager;
    @NonNull private final PaymentSettingRepository paymentSettingRepository;

    public EscPaymentManagerImp(@NonNull final ESCManagerBehaviour escManager,
        @NonNull final PaymentSettingRepository paymentSettingRepository) {
        this.escManager = escManager;
        this.paymentSettingRepository = paymentSettingRepository;
    }

    @Override
    public boolean hasEsc(@NonNull final Card card) {
        return !TextUtil.isEmpty(escManager.getESC(card.getId(), card.getFirstSixDigits(), card.getLastFourDigits()));
    }

    @Override
    public boolean manageEscForPayment(final List<PaymentData> paymentDataList, final String paymentStatus,
        final String paymentStatusDetail) {

        boolean isInvalidEsc = false;
        for (final PaymentData paymentData : paymentDataList) {
            if (EscUtil
                .shouldDeleteEsc(paymentSettingRepository.getConfiguration().getEscBlacklistedStatus(), paymentData,
                    paymentStatus, paymentStatusDetail)) {
                escManager.deleteESCWith(paymentData.getToken().getCardId(), EscDeleteReason.REJECTED_PAYMENT,
                    paymentStatusDetail);
            } else if (EscUtil
                .shouldStoreESC(paymentSettingRepository.getConfiguration().getEscBlacklistedStatus(), paymentData,
                    paymentStatus, paymentStatusDetail)) {
                escManager.saveESCWith(paymentData.getToken().getCardId(), paymentData.getToken().getEsc());
            }

            isInvalidEsc |= EscUtil.isInvalidEscPayment(paymentData, paymentStatus, paymentStatusDetail);
        }

        return isInvalidEsc;
    }

    @Override
    public boolean manageEscForError(final MercadoPagoError error, final List<PaymentData> paymentDataList) {
        boolean result = false;

        for (final PaymentData paymentData : paymentDataList) {
            final boolean isInvalidEsc = paymentData.containsCardInfo() && EscUtil.isErrorInvalidPaymentWithEsc(error);
            if (isInvalidEsc) {
                escManager.deleteESCWith(paymentData.getToken().getCardId(), EscDeleteReason.REJECTED_PAYMENT,
                    ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC);
            }
            result |= isInvalidEsc;
        }

        return result;
    }
}