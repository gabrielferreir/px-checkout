package com.mercadopago.android.px.internal.viewmodel.drawables;

import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.model.NewCardMetadata;
import com.mercadopago.android.px.model.OfflinePaymentTypesMetadata;

public class OtherPaymentMethodFragmentItem extends DrawableFragmentItem {

    @Nullable private final NewCardMetadata newCardMetadata;
    @Nullable private final OfflinePaymentTypesMetadata offlineMethodsMetadata;

    public static final Creator<OtherPaymentMethodFragmentItem> CREATOR =
        new Creator<OtherPaymentMethodFragmentItem>() {
            @Override
            public OtherPaymentMethodFragmentItem createFromParcel(final Parcel in) {
                return new OtherPaymentMethodFragmentItem(in);
            }

            @Override
            public OtherPaymentMethodFragmentItem[] newArray(final int size) {
                return new OtherPaymentMethodFragmentItem[size];
            }
        };

    /* default */ OtherPaymentMethodFragmentItem(@NonNull final Parameters parameters,
        @Nullable final NewCardMetadata newCardMetadata,
        @Nullable final OfflinePaymentTypesMetadata offlineMethodsMetadata) {
        super(parameters);
        this.newCardMetadata = newCardMetadata;
        this.offlineMethodsMetadata = offlineMethodsMetadata;
    }

    protected OtherPaymentMethodFragmentItem(final Parcel in) {
        super(in);
        newCardMetadata = in.readParcelable(NewCardMetadata.class.getClassLoader());
        offlineMethodsMetadata = in.readParcelable(OfflinePaymentTypesMetadata.class.getClassLoader());
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(newCardMetadata, flags);
        dest.writeParcelable(offlineMethodsMetadata, flags);
    }

    @Nullable
    public NewCardMetadata getNewCardMetadata() {
        return newCardMetadata;
    }

    @Nullable
    public OfflinePaymentTypesMetadata getOfflineMethodsMetadata() {
        return offlineMethodsMetadata;
    }

    @Override
    public Fragment draw(@NonNull final PaymentMethodFragmentDrawer drawer) {
        return drawer.draw(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}