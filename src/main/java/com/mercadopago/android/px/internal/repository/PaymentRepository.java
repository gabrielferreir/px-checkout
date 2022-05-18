package com.mercadopago.android.px.internal.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceEventHandler;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.PaymentRecovery;

public interface PaymentRepository {

    @Nullable
    PaymentServiceEventHandler getObservableEvents();

    void startExpressPayment();

    boolean isExplodingAnimationCompatible();

    int getPaymentTimeout();

    void storePayment(@NonNull final IPaymentDescriptor iPayment);

    @Nullable
    IPaymentDescriptor getPayment();

    @NonNull
    PaymentRecovery createRecoveryForInvalidESC();

    @NonNull
    PaymentRecovery createPaymentRecovery();

    void reset();
}