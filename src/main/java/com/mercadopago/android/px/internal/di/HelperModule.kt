package com.mercadopago.android.px.internal.di

import com.mercadopago.android.px.internal.features.one_tap.add_new_card.CardViewHelper
import com.mercadopago.android.px.internal.features.payment_result.model.DisplayInfoHelper
import com.mercadopago.android.px.tracking.internal.BankInfoHelper

internal class HelperModule {
    val displayInfoHelper: DisplayInfoHelper
        get() {
            val session = Session.getInstance()
            return DisplayInfoHelper(session.payerPaymentMethodRepository, session.configurationModule.userSelectionRepository)
        }

    val bankInfoHelper: BankInfoHelper
        get() {
            val session = Session.getInstance()
            return BankInfoHelper(session.payerPaymentMethodRepository)
        }

    val cardViewHelper: CardViewHelper
        get() = CardViewHelper()
}
