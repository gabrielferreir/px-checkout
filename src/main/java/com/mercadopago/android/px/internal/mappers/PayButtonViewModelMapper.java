package com.mercadopago.android.px.internal.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel;
import com.mercadopago.android.px.model.internal.CustomTexts;

public class PayButtonViewModelMapper extends Mapper<CustomTexts, PayButtonViewModel> {

    @Override
    public PayButtonViewModel map(@NonNull final CustomTexts customTexts) {
        return new PayButtonViewModel(customTexts.getPayButton(), customTexts.getPayButtonProgress());
    }
}