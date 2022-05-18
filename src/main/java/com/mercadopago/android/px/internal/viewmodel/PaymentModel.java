package com.mercadopago.android.px.internal.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.viewmodel.handlers.PaymentModelHandler;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.internal.CongratsResponse;
import com.mercadopago.android.px.model.internal.remedies.RemediesResponse;

public class PaymentModel implements Parcelable {

    public static final Creator<PaymentModel> CREATOR = new Creator<PaymentModel>() {
        @Override
        public PaymentModel createFromParcel(final Parcel in) {
            return new PaymentModel(in);
        }

        @Override
        public PaymentModel[] newArray(final int size) {
            return new PaymentModel[size];
        }
    };

    private final IPaymentDescriptor payment;
    private final PaymentResult paymentResult;
    private final CongratsResponse congratsResponse;
    private final RemediesResponse remedies;
    private final Currency currency;

    public PaymentModel(@NonNull final PaymentResult paymentResult, @NonNull final Currency currency) {
        this(null, paymentResult, CongratsResponse.EMPTY, RemediesResponse.getEMPTY(), currency);
    }

    public PaymentModel(@Nullable final IPaymentDescriptor payment, @NonNull final PaymentResult paymentResult,
        @NonNull final CongratsResponse congratsResponse, @NonNull final RemediesResponse remedies,
        @NonNull final Currency currency) {
        this.payment = payment;
        this.paymentResult = paymentResult;
        this.congratsResponse = congratsResponse;
        this.remedies = remedies;
        this.currency = currency;
    }

    @Nullable
    public IPaymentDescriptor getPayment() {
        return payment;
    }

    @NonNull
    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    @NonNull
    public Currency getCurrency() {
        return currency;
    }

    @NonNull
    public CongratsResponse getCongratsResponse() {
        return congratsResponse;
    }

    @NonNull
    public RemediesResponse getRemedies() {
        return remedies;
    }

    public void process(@NonNull final PaymentModelHandler handler) {
        handler.visit(this);
    }

    /* default */ PaymentModel(final Parcel in) {
        payment = (IPaymentDescriptor) in.readSerializable();
        paymentResult = (PaymentResult) in.readSerializable();
        congratsResponse = in.readParcelable(CongratsResponse.class.getClassLoader());
        remedies = in.readParcelable(RemediesResponse.class.getClassLoader());
        currency = in.readParcelable(Currency.class.getClassLoader());
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeSerializable(payment);
        dest.writeSerializable(paymentResult);
        dest.writeParcelable(congratsResponse, flags);
        dest.writeParcelable(remedies, flags);
        dest.writeParcelable(currency, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}