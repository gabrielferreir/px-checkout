package com.mercadopago.android.px.internal.util;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.model.EscValidationData;
import com.mercadopago.android.px.addons.model.SecurityValidationData;
import com.mercadopago.android.px.internal.core.ProductIdProvider;
import com.mercadopago.android.px.internal.mappers.PaymentMethodReauthMapper;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.tracking.TrackingRepository;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.internal.PaymentConfiguration;
import java.util.List;
import java.util.Objects;

public final class SecurityValidationDataFactory {
    @NonNull final ProductIdProvider productIdProvider;
    @NonNull final PaymentSettingRepository paymentSettingRepository;
    @NonNull final TrackingRepository trackingRepository;
    @NonNull final PaymentMethodReauthMapper paymentMethodReauthMapper;

    private static final String AMOUNT_PARAM  = "amount";
    private static final String PAYMENT_METHODS_PARAM  = "payment_methods";
    private static final String SESSION_ID_PARAM  = "session_id";

    public SecurityValidationDataFactory(
        @NonNull final ProductIdProvider productIdProvider,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final TrackingRepository trackingRepository,
        @NonNull final PaymentMethodReauthMapper paymentMethodReauthMapper
    ) {
        this.productIdProvider = productIdProvider;
        this.paymentSettingRepository = paymentSettingRepository;
        this.trackingRepository = trackingRepository;
        this.paymentMethodReauthMapper = paymentMethodReauthMapper;
    }

    public SecurityValidationData create(@NonNull final PaymentConfiguration paymentConfiguration, @NonNull final List<PaymentData> paymentMethods) {
        final String customOptionId = paymentConfiguration.getCustomOptionId();
        final boolean securityCodeRequired = paymentConfiguration.getSecurityCodeRequired();
        final EscValidationData escValidationData = new EscValidationData.Builder(customOptionId, securityCodeRequired)
                .build();
        return new SecurityValidationData
                .Builder(productIdProvider.getProductId())
                .putParam(AMOUNT_PARAM, Objects.requireNonNull(paymentSettingRepository.getCheckoutPreference()).getTotalAmount())
                .putParam(SESSION_ID_PARAM, trackingRepository.getSessionId())
                .putParam(PAYMENT_METHODS_PARAM, paymentMethodReauthMapper.map(paymentMethods))
                .setEscValidationData(escValidationData)
                .build();
    }
}
