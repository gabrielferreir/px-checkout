package com.mercadopago.android.px.internal.viewmodel.drawables;

import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.model.ConsumerCreditsMetadata;
import com.mercadopago.android.px.model.internal.Text;

public class ConsumerCreditsDrawableFragmentItem extends DrawableFragmentItem {

    @NonNull public final ConsumerCreditsMetadata metadata;
    @Nullable public Text tag;

    public static final Creator<ConsumerCreditsDrawableFragmentItem> CREATOR =
        new Creator<ConsumerCreditsDrawableFragmentItem>() {
            @Override
            public ConsumerCreditsDrawableFragmentItem createFromParcel(final Parcel in) {
                return new ConsumerCreditsDrawableFragmentItem(in);
            }

            @Override
            public ConsumerCreditsDrawableFragmentItem[] newArray(final int size) {
                return new ConsumerCreditsDrawableFragmentItem[size];
            }
        };

    public ConsumerCreditsDrawableFragmentItem(@NonNull final Parameters parameters,
        @NonNull final ConsumerCreditsMetadata metadata, @Nullable Text tag) {
        super(parameters);
        this.metadata = metadata;
        this.tag = tag;
    }

    protected ConsumerCreditsDrawableFragmentItem(final Parcel in) {
        super(in);
        metadata = in.readParcelable(ConsumerCreditsMetadata.class.getClassLoader());
        tag = in.readParcelable(Text.class.getClassLoader());
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(metadata, flags);
        dest.writeParcelable(tag, flags);
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