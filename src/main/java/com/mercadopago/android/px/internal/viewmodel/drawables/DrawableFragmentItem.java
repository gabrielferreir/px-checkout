package com.mercadopago.android.px.internal.viewmodel.drawables;

import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.meli.android.carddrawer.model.customview.SwitchModel;
import com.mercadopago.android.px.internal.util.KParcelable;
import com.mercadopago.android.px.model.Reimbursement;
import com.mercadopago.android.px.model.internal.Text;
import java.io.Serializable;

public class DrawableFragmentItem implements KParcelable, Serializable {

    private final DrawableFragmentCommons.ByApplication commonsByApplication;
    private final Text bottomDescription;
    private final Reimbursement reimbursement;
    private SwitchModel switchModel;

    protected DrawableFragmentItem(@NonNull final Parameters parameters) {
        commonsByApplication = parameters.commonsByApplication;
        bottomDescription = parameters.bottomDescription;
        reimbursement = parameters.reimbursement;
        switchModel = parameters.switchModel;
    }

    protected DrawableFragmentItem(final Parcel in) {
        commonsByApplication = in.readParcelable(DrawableFragmentCommons.ByApplication.class.getClassLoader());
        bottomDescription = in.readParcelable(Text.class.getClassLoader());
        reimbursement = in.readParcelable(Reimbursement.class.getClassLoader());
        switchModel = in.readParcelable(SwitchModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(commonsByApplication, flags);
        dest.writeParcelable(bottomDescription, flags);
        dest.writeParcelable(reimbursement, flags);
        dest.writeParcelable(switchModel, flags);
    }

    public void setSwitchModel(final SwitchModel switchModel) {
        this.switchModel = switchModel;
    }

    public Fragment draw(@NonNull final PaymentMethodFragmentDrawer drawer) {
        return drawer.draw(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public DrawableFragmentCommons.ByApplication getCommonsByApplication() {
        return commonsByApplication;
    }

    @Nullable
    public Text getBottomDescription() {
        return bottomDescription;
    }

    @Nullable
    public Reimbursement getReimbursement() {
        return reimbursement;
    }

    public boolean shouldHighlightBottomDescription() {
        return bottomDescription == null;
    }

    @Nullable
    public SwitchModel getSwitchModel() {
        return switchModel;
    }

    public static final class Parameters {
        /* default */ @NonNull final DrawableFragmentCommons.ByApplication commonsByApplication;
        /* default */ @Nullable final Text bottomDescription;
        /* default */ @Nullable final Reimbursement reimbursement;
        /* default */ @Nullable final SwitchModel switchModel;

        /* default */ Parameters(@NonNull final DrawableFragmentCommons.ByApplication commonsByApplication,
            @Nullable final Text bottomDescription, @Nullable final Reimbursement reimbursement,
            @Nullable final SwitchModel switchModel) {
            this.commonsByApplication = commonsByApplication;
            this.bottomDescription = bottomDescription;
            this.reimbursement = reimbursement;
            this.switchModel = switchModel;
        }
    }

    public static final Creator<DrawableFragmentItem> CREATOR = new Creator<DrawableFragmentItem>() {
        @Override
        public DrawableFragmentItem createFromParcel(final Parcel in) {
            return new DrawableFragmentItem(in);
        }

        @Override
        public DrawableFragmentItem[] newArray(final int size) {
            return new DrawableFragmentItem[size];
        }
    };
}
