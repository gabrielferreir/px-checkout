package com.mercadopago.android.px.internal.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

public final class SplitSelectionState implements Parcelable {

    private boolean userWantsToSplit;
    private boolean preferDefault = true;

    public SplitSelectionState() {
    }

    public boolean preferDefault() {
        return preferDefault;
    }

    public boolean userWantsToSplit() {
        return userWantsToSplit;
    }

    public void setUserWantsToSplit(final boolean userWantsToSplit) {
        preferDefault = false;
        this.userWantsToSplit = userWantsToSplit;
    }

    protected SplitSelectionState(final Parcel in) {
        userWantsToSplit = in.readByte() != 0;
        preferDefault = in.readByte() != 0;
    }

    public static final Creator<SplitSelectionState> CREATOR = new Creator<SplitSelectionState>() {
        @Override
        public SplitSelectionState createFromParcel(final Parcel in) {
            return new SplitSelectionState(in);
        }

        @Override
        public SplitSelectionState[] newArray(final int size) {
            return new SplitSelectionState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeByte((byte) (userWantsToSplit ? 1 : 0));
        parcel.writeByte((byte) (preferDefault ? 1 : 0));
    }
}
