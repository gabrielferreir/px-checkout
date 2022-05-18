package com.mercadopago.android.px.internal.features.one_tap.installments;

import android.view.View;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ViewUtils;

//TODO unify with normal installments.

public class InstallmentRowHolderV2 extends InstallmentRowHolder {

    private final RadioButton radioButton;

    /* default */ InstallmentRowHolderV2(final View itemView) {
        super(itemView);
        radioButton = itemView.findViewById(R.id.radio_button);
    }

    @Override
    protected boolean loadBottomText(@NonNull final Model model, final int visibility) {
        return ViewUtils.loadOrHide(visibility, model.payerCost.getInterestRate(), bottomText);
    }

    /* default */ void highLight() {
        radioButton.setChecked(true);
    }

    /* default */ void noHighLight() {
        radioButton.setChecked(false);
    }
}