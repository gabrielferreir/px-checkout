package com.mercadopago.android.px.internal.datasource;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.core.SplitPaymentProcessor;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.RetrofitUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.util.HashMap;
import java.util.Map;

public final class DefaultPaymentProcessor implements SplitPaymentProcessor {

    /* default */ DefaultPaymentProcessor() {
    }

    @Override
    public void startPayment(@NonNull final Context context, @NonNull final CheckoutData data,
        @NonNull final OnPaymentListener paymentListener) {
        final Map<String, Object> body = new HashMap<>();
        final String prefId = data.checkoutPreference.getId();
        if (TextUtil.isEmpty(prefId)) {
            throw new IllegalStateException("This processor can't be used with open preferences");
        }
        body.put("merchant_order_id", data.checkoutPreference.getMerchantOrderId());
        body.put("pref_id", prefId);
        body.put("payment_data", data.paymentDataList);
        body.put("validation_program_id", data.validationProgramId);
        final Session session = Session.getInstance();
        final String transactionId = session.getConfigurationModule().getPaymentSettings().getTransactionId();
        session.getMercadoPagoServices().createPayment(transactionId, data.securityType, body,
            new TaggedCallback<Payment>(ApiUtil.RequestOrigin.CREATE_PAYMENT) {
                @Override
                public void onSuccess(final Payment payment) {
                    paymentListener.onPaymentFinished(payment);
                }

                @Override
                public void onFailure(final MercadoPagoError error) {
                    paymentListener.onPaymentError(error);
                }
            });
    }

    @Override
    public int getPaymentTimeout(@NonNull final CheckoutPreference checkoutPreference) {
        return RetrofitUtil.DEFAULT_READ_TIMEOUT * 1000;
    }

    @Override
    public boolean shouldShowFragmentOnPayment(@NonNull final CheckoutPreference checkoutPreference) {
        return false;
    }

    @Override
    public boolean supportsSplitPayment(@Nullable final CheckoutPreference checkoutPreference) {
        return false;
    }

    @Nullable
    @Override
    public Fragment getFragment(@NonNull final CheckoutData data, @NonNull final Context context) {
        return null;
    }

    public static final Creator<DefaultPaymentProcessor> CREATOR = new Creator<DefaultPaymentProcessor>() {
        @Override
        public DefaultPaymentProcessor createFromParcel(final Parcel in) {
            return new DefaultPaymentProcessor(in);
        }

        @Override
        public DefaultPaymentProcessor[] newArray(final int size) {
            return new DefaultPaymentProcessor[size];
        }
    };

    /* default */ DefaultPaymentProcessor(final Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
    }
}