package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.utils.ResourcesUtil;
import java.util.ArrayList;
import java.util.List;

public enum PaymentMethodStub implements JsonInjectable<PaymentMethod> {
    AMEX_CREDIT("pm_amex_credit.json", PaymentTypes.CREDIT_CARD, Sites.ARGENTINA.getId()),
    VISA_CREDIT("pm_visa_credit.json", PaymentTypes.CREDIT_CARD, Sites.ARGENTINA.getId()),
    MASTER_CREDIT("pm_master_credit.json", PaymentTypes.CREDIT_CARD, Sites.ARGENTINA.getId()),
    CORDIAL_CREDIT("pm_cordial_credit.json", PaymentTypes.CREDIT_CARD, Sites.ARGENTINA.getId()),
    VISA_DEBIT("pm_visa_debit.json", PaymentTypes.DEBIT_CARD, Sites.ARGENTINA.getId()),
    MASTER_DEBIT("pm_master_debit.json", PaymentTypes.DEBIT_CARD, Sites.ARGENTINA.getId()),
    RAPIPAGO_OFF("pm_pagofacil_off.json", PaymentTypes.TICKET, Sites.ARGENTINA.getId()),
    PAGOFACIL_OFF("pm_rapipago_off.json", PaymentTypes.TICKET, Sites.ARGENTINA.getId()),
    CARGA_VIRTUAL_OFF("pm_carga_virtual.json", PaymentTypes.TICKET, Sites.ARGENTINA.getId()),
    BOLBRADESCO_OFF("pm_bolbradesco.json", PaymentTypes.TICKET, Sites.BRASIL.getId()),
    CONSUMER_CREDIT("pm_consumer_credit.json", PaymentTypes.DIGITAL_CURRENCY, Sites.ARGENTINA.getId());

    public static PaymentMethodStub[] MEDIOS_OFF_MLA = { RAPIPAGO_OFF, PAGOFACIL_OFF, CARGA_VIRTUAL_OFF };
    public static PaymentMethodStub[] MEDIOS_OFF_MLB = { BOLBRADESCO_OFF };
    public static PaymentMethodStub[] ONLY_CREDIT = { VISA_CREDIT, MASTER_CREDIT, AMEX_CREDIT };

    @NonNull private final String fileName;
    @NonNull private final String paymentTypeId;
    @NonNull private final String siteId;

    public static List<PaymentMethod> getAll(@Nullable final String... paymentTypes) {
        final List<PaymentMethod> results = new ArrayList<>();
        if (paymentTypes != null) {
            for (final PaymentMethodStub stub : values()) {
                for (final String paymentTypeId : paymentTypes) {
                    if (stub.paymentTypeId.equals(paymentTypeId)) {
                        results.add(stub.get());
                    }
                }
            }
        }
        return results;
    }

    public static List<PaymentMethod> getAllBySite(@NonNull final String siteId) {
        final List<PaymentMethod> results = new ArrayList<>();
        for (final PaymentMethodStub stub : values()) {
            if (stub.siteId.equals(siteId)) {
                results.add(stub.get());
            }
        }
        return results;
    }

    PaymentMethodStub(@NonNull final String fileName, @NonNull final String paymentTypeId,
        @NonNull final String siteId) {
        this.fileName = fileName;
        this.paymentTypeId = paymentTypeId;
        this.siteId = siteId;
    }

    @NonNull
    public PaymentMethod get() {
        return JsonUtil.fromJson(getJson(), PaymentMethod.class);
    }

    @NonNull
    @Override
    public String getJson() {
        return ResourcesUtil.getStringResource(fileName);
    }

    @NonNull
    @Override
    public String getType() {
        return "%PAYMENT_METHOD%";
    }
}