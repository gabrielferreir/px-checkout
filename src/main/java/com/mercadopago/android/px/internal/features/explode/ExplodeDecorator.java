package com.mercadopago.android.px.internal.features.explode;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultDecorator;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultType;

public final class ExplodeDecorator implements Parcelable {

    public static ExplodeDecorator from(@NonNull final PaymentResultType type) {
        return new ExplodeDecorator(type.resColor, type.icon);
    }

    public static ExplodeDecorator from(@NonNull final PaymentResultDecorator paymentResultDecorator) {
        return new ExplodeDecorator(paymentResultDecorator.getPrimaryColor(), paymentResultDecorator.getStatusIcon());
    }

    private final int primaryColor;
    private final int statusIcon;

    /* default */ ExplodeDecorator(@ColorRes final int primaryColor, @DrawableRes final int statusIcon) {
        this.primaryColor = primaryColor;
        this.statusIcon = statusIcon;
    }

    /* default */ ExplodeDecorator(final Parcel in) {
        primaryColor = in.readInt();
        statusIcon = in.readInt();
    }

    public static final Creator<ExplodeDecorator> CREATOR = new Creator<ExplodeDecorator>() {
        @Override
        public ExplodeDecorator createFromParcel(final Parcel in) {
            return new ExplodeDecorator(in);
        }

        @Override
        public ExplodeDecorator[] newArray(final int size) {
            return new ExplodeDecorator[size];
        }
    };

    @ColorInt
    public int getPrimaryColor(@NonNull final Context context) {
        return ContextCompat.getColor(context, primaryColor);
    }

    @ColorInt
    public int getDarkPrimaryColor(@NonNull final Context context) {
        return ViewUtils.getDarkPrimaryColor(getPrimaryColor(context));
    }

    @DrawableRes
    public int getStatusIcon() {
        return statusIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(primaryColor);
        dest.writeInt(statusIcon);
    }
}