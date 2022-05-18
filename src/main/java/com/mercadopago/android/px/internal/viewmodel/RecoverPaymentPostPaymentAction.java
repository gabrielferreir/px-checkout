package com.mercadopago.android.px.internal.viewmodel;

import android.os.Parcel;
import androidx.annotation.NonNull;

public class RecoverPaymentPostPaymentAction extends PostPaymentAction {

    public RecoverPaymentPostPaymentAction() {
        super(RequiredAction.RECOVER_PAYMENT);
    }

    /* default */ RecoverPaymentPostPaymentAction(final Parcel in) {
        super(in);
    }

    @Override
    public void execute(@NonNull final ActionController actionController) {
        actionController.recoverPayment(this);
    }

    public static final Creator<RecoverPaymentPostPaymentAction> CREATOR =
        new Creator<RecoverPaymentPostPaymentAction>() {
            @Override
            public RecoverPaymentPostPaymentAction createFromParcel(final Parcel in) {
                return new RecoverPaymentPostPaymentAction(in);
            }

            @Override
            public RecoverPaymentPostPaymentAction[] newArray(final int size) {
                return new RecoverPaymentPostPaymentAction[size];
            }
        };
}
