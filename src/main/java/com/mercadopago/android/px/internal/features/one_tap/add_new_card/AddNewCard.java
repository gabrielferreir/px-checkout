package com.mercadopago.android.px.internal.features.one_tap.add_new_card;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.util.CardFormWrapper;
import com.mercadopago.android.px.model.CardFormInitType;

/* default */ interface AddNewCard {

    interface View extends MvpView {
        void startCardForm(@NonNull final CardFormWrapper cardFormWrapper, @NonNull final CardFormInitType initType);
        void launchDeepLink(@NonNull final String deepLink);
        void onNewCardWithSheetOptions();
    }
}
