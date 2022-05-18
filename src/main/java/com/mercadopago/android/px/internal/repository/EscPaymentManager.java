package com.mercadopago.android.px.internal.repository;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

public interface EscPaymentManager {

    /**
     * Verify if exists ESC saved for certain card.
     *
     * @param card your card
     * @return true if has ESC, false otherwise.
     */
    boolean hasEsc(@NonNull final Card card);

    /**
     * Resolve ESC for transaction - delete it if needed
     *
     * @param paymentDataList list of payment data
     * @param paymentStatus the payment status
     * @param paymentStatusDetail the payment detail related with the status
     * @return true if esc is invalid
     */
    boolean manageEscForPayment(final List<PaymentData> paymentDataList,
        final String paymentStatus,
        final String paymentStatusDetail);

    /**
     * Resolve ESC for transaction - delete it if needed.
     *
     * @param paymentData the payment information
     * @param error the payment error
     * @return isInvalidEsc
     */
    boolean manageEscForError(final MercadoPagoError error, final List<PaymentData> paymentData);
}
