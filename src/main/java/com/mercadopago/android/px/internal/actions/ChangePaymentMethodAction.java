package com.mercadopago.android.px.internal.actions;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.Action;

public class ChangePaymentMethodAction extends Action {

    @NonNull
    @Override
    public String toString() {
        return "Cambiar medio de pago";
    }
}
