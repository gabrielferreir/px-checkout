package com.mercadopago.android.px.internal.callbacks;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.core.internal.OnPaymentListener;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.tracking.internal.model.Reason;

public interface PaymentServiceHandler extends OnPaymentListener {

    /**
     * When payment processor has visual interaction this method will be called.
     */
    void onVisualPayment();

    /**
     * If payment was reject by invalid esc this method will be called.
     *
     * @param recovery
     */
    void onRecoverPaymentEscInvalid(final PaymentRecovery recovery);

    default void onPaymentFinished(@NonNull final IPaymentDescriptor payment) {}

    void onPostPayment(@NonNull final PaymentModel paymentModel);

    void onPostPaymentFlowStarted(@NonNull final IPaymentDescriptor iPaymentDescriptor);
}
