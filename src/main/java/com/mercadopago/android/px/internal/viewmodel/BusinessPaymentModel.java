package com.mercadopago.android.px.internal.viewmodel;

import android.os.Parcel;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.viewmodel.handlers.PaymentModelHandler;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.internal.CongratsResponse;
import com.mercadopago.android.px.model.internal.remedies.RemediesResponse;

public class BusinessPaymentModel extends PaymentModel {

    private final BusinessPayment businessPayment;

    public BusinessPaymentModel(@NonNull final BusinessPayment businessPayment,
        @NonNull final PaymentResult paymentResult, @NonNull final CongratsResponse congratsResponse,
        @NonNull final RemediesResponse remedies, @NonNull final Currency currency) {
        super(null, paymentResult, congratsResponse, remedies, currency);
        this.businessPayment = businessPayment;
    }

    @NonNull
    @Override
    public BusinessPayment getPayment() {
        return businessPayment;
    }

    /* default */ BusinessPaymentModel(final Parcel in) {
        super(in);
        businessPayment = in.readParcelable(BusinessPayment.class.getClassLoader());
    }

    @Override
    public void process(@NonNull final PaymentModelHandler handler) {
        handler.visit(this);
    }

    public static final Creator<BusinessPaymentModel> CREATOR = new Creator<BusinessPaymentModel>() {
        @Override
        public BusinessPaymentModel createFromParcel(final Parcel in) {
            return new BusinessPaymentModel(in);
        }

        @Override
        public BusinessPaymentModel[] newArray(final int size) {
            return new BusinessPaymentModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(businessPayment, flags);
    }
}