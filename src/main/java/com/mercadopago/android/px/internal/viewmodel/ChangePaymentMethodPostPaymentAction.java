package com.mercadopago.android.px.internal.viewmodel;

import android.os.Parcel;
import androidx.annotation.NonNull;

public class ChangePaymentMethodPostPaymentAction extends PostPaymentAction {

    public ChangePaymentMethodPostPaymentAction() {
        super(RequiredAction.SELECT_OTHER_PAYMENT_METHOD);
    }

    /* default */ ChangePaymentMethodPostPaymentAction(final Parcel in) {
        super(in);
    }

    @Override
    public void execute(@NonNull final ActionController actionController) {
        actionController.onChangePaymentMethod();
    }

    public static final Creator<ChangePaymentMethodPostPaymentAction> CREATOR =
        new Creator<ChangePaymentMethodPostPaymentAction>() {
            @Override
            public ChangePaymentMethodPostPaymentAction createFromParcel(final Parcel in) {
                return new ChangePaymentMethodPostPaymentAction(in);
            }

            @Override
            public ChangePaymentMethodPostPaymentAction[] newArray(final int size) {
                return new ChangePaymentMethodPostPaymentAction[size];
            }
        };
}