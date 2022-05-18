package com.mercadopago.android.px.core;

import android.content.Context;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.fragment.app.DialogFragment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.util.Collections;
import java.util.List;

public interface DynamicDialogCreator extends Parcelable {

    /* default */ final class CheckoutData {

        @NonNull public final PaymentData paymentData;
        @NonNull public final CheckoutPreference checkoutPreference;
        @NonNull public final List<PaymentData> paymentDataList;

        @Deprecated
        public CheckoutData(@NonNull final CheckoutPreference checkoutPreference,
            @NonNull final PaymentData paymentData) {
            this.paymentData = paymentData;
            this.checkoutPreference = checkoutPreference;
            paymentDataList = Collections.singletonList(paymentData);
        }

        public CheckoutData(@NonNull final CheckoutPreference checkoutPreference,
            @NonNull @Size(min = 1) final List<PaymentData> paymentData) {
            this.paymentData = paymentData.get(0);
            paymentDataList = paymentData;
            this.checkoutPreference = checkoutPreference;
        }
    }

    /**
     * if true is returned then create will be called and dialog will be
     * asked to be shown.
     *
     * @param checkoutData
     * @return if dialog should be shown.
     */
    boolean shouldShowDialog(@NonNull final Context context, @NonNull final CheckoutData checkoutData);

    /**
     * @param checkoutData available data
     * @return yourDialog
     */
    @NonNull
    DialogFragment create(@NonNull final Context context, @NonNull final CheckoutData checkoutData);
}