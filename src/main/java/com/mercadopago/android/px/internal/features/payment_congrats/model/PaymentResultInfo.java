package com.mercadopago.android.px.internal.features.payment_congrats.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

public final class PaymentResultInfo implements Parcelable{

    @Nullable private final String title;
    @Nullable private final String subtitle;

    public PaymentResultInfo(@Nullable final String title, @Nullable final String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getSubtitle() {
        return subtitle;
    }

    public static final Creator<PaymentResultInfo> CREATOR = new Creator<PaymentResultInfo>() {
        @Override
        public PaymentResultInfo createFromParcel(final Parcel in) {
            return new PaymentResultInfo(in);
        }

        @Override
        public PaymentResultInfo[] newArray(final int size) {
            return new PaymentResultInfo[size];
        }
    };

    protected PaymentResultInfo(final Parcel in) {
        title = in.readString();
        subtitle = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(title);
        dest.writeString(subtitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
