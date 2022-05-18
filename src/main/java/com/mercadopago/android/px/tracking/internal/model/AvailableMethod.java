package com.mercadopago.android.px.tracking.internal.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.PaymentMethod;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used for Payment vault and Express checkout screen.
 */
@SuppressWarnings("unused")
@Keep
public class AvailableMethod extends TrackingMapModel implements Parcelable {

    @Nullable
    /* default */ final String paymentMethodId;
    @NonNull
    /* default */ final String paymentMethodType;
    @Nullable
    /* default */ final Map<String, Object> extraInfo;

    public static final Creator<AvailableMethod> CREATOR = new Creator<AvailableMethod>() {
        @Override
        public AvailableMethod createFromParcel(final Parcel in) {
            return new AvailableMethod(in);
        }

        @Override
        public AvailableMethod[] newArray(final int size) {
            return new AvailableMethod[size];
        }
    };

    public static AvailableMethod from(@NonNull final PaymentMethod paymentMethod) {
        return new AvailableMethod(paymentMethod.getId(), paymentMethod.getPaymentTypeId());
    }

    public AvailableMethod(@NonNull final String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
        paymentMethodId = null;
        extraInfo = null;
    }

    public AvailableMethod(@NonNull final String paymentMethodId,
        @NonNull final String paymentMethodType,
        @NonNull final Map<String, Object> extraInfo) {
        this.paymentMethodId = paymentMethodId;
        this.paymentMethodType = paymentMethodType;
        this.extraInfo = extraInfo;
    }

    public AvailableMethod(@Nullable final String paymentMethodId,
        @NonNull final String paymentMethodType) {
        this.paymentMethodId = paymentMethodId;
        this.paymentMethodType = paymentMethodType;
        extraInfo = null;
    }

    public AvailableMethod(final Builder builder) {
        paymentMethodId = builder.paymentMethodId;
        paymentMethodType = builder.paymentMethodType;
        extraInfo = builder.extraInfo;
    }

    protected AvailableMethod(final Parcel in) {
        paymentMethodId = in.readString();
        paymentMethodType = in.readString();
        extraInfo = new HashMap<>();
        in.readMap(extraInfo, Object.class.getClassLoader());
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int flags) {
        parcel.writeString(paymentMethodId);
        parcel.writeString(paymentMethodType);
        parcel.writeMap(extraInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static class Builder {
        private static final String HAS_INTEREST_FREE = "has_interest_free";
        private static final String HAS_REIMBURSEMENT = "has_reimbursement";
        private static final String APPLICATIONS = "methods_applications";
        String paymentMethodId;
        String paymentMethodType;
        Map<String, Object> extraInfo = new HashMap<>();

        public Builder(@NonNull final String paymentMethodId, @NonNull final String paymentMethodType,
            final boolean hasInterestFree, final boolean hasReimbursement,
            @NonNull final List<ApplicationInfo> applicationsInfo) {

            this.paymentMethodId = paymentMethodId;
            this.paymentMethodType = paymentMethodType;
            extraInfo.put(HAS_INTEREST_FREE, hasInterestFree);
            extraInfo.put(HAS_REIMBURSEMENT, hasReimbursement);
            extraInfo.put(APPLICATIONS, applicationsInfo);
        }

        public Builder setExtraInfo(@NonNull final Map<String, Object> extraInfo) {
            this.extraInfo.putAll(extraInfo);
            return this;
        }

        public AvailableMethod build() {
            return new AvailableMethod(this);
        }
    }
}