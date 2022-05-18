package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.TextUtil;

public final class PayButtonViewModel implements Parcelable {

    @Nullable /* default */ final String buttonText;
    @Nullable /* default */ final String buttonProgressText;

    private final ILocalizedCharSequence buttonTextLocalized = new ILocalizedCharSequence() {
        @Override
        public CharSequence get(@NonNull final Context context) {
            return TextUtil.isNotEmpty(buttonText) ? buttonText : context.getString(R.string.px_pay);
        }
    };

    private final ILocalizedCharSequence buttonProgressTextLocalized = new ILocalizedCharSequence() {
        @Override
        public CharSequence get(@NonNull final Context context) {
            return TextUtil.isNotEmpty(buttonProgressText) ? buttonProgressText :
                context.getString(R.string.px_processing_payment_button);
        }
    };

    public PayButtonViewModel(@Nullable final String buttonText, @Nullable final String buttonProgressText) {
        this.buttonText = buttonText;
        this.buttonProgressText = buttonProgressText;
    }

    @NonNull
    public CharSequence getButtonText(@NonNull final Context context) {
        return buttonTextLocalized.get(context);
    }

    @NonNull
    public CharSequence getButtonProgressText(@NonNull final Context context) {
        return buttonProgressTextLocalized.get(context);
    }

    /* default */ PayButtonViewModel(final Parcel in) {
        buttonText = in.readString();
        buttonProgressText = in.readString();
    }

    public static final Creator<PayButtonViewModel> CREATOR = new Creator<PayButtonViewModel>() {
        @Override
        public PayButtonViewModel createFromParcel(final Parcel in) {
            return new PayButtonViewModel(in);
        }

        @Override
        public PayButtonViewModel[] newArray(final int size) {
            return new PayButtonViewModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(buttonText);
        dest.writeString(buttonProgressText);
    }
}