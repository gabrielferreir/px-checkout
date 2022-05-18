package com.mercadopago.android.px.internal.features.one_tap.add_new_card

import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker.Companion.with
import com.mercadopago.android.px.internal.util.CardFormWrapper
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.internal.base.BasePresenter
import com.mercadopago.android.px.model.NewCardMetadata
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import com.mercadopago.android.px.tracking.internal.TrackWrapper

internal class OtherPaymentMethodPresenter(
    private val cardFormWrapper: CardFormWrapper,
    tracker: MPTracker
) : BasePresenter<AddNewCard.View>(tracker) {

    fun onTrackActivityNotFoundFriction(error: MercadoPagoError) {
        track(
            with(
                "${TrackWrapper.BASE_PATH}/bank_account_added/",
                FrictionEventTracker.Id.GENERIC,
                FrictionEventTracker.Style.SCREEN,
                error
            )
        )
    }

    fun onNewCardActions(newCardMetadata: NewCardMetadata) = when {
        newCardMetadata.deepLink != null -> view.launchDeepLink(newCardMetadata.deepLink!!)
        newCardMetadata.sheetOptions == null -> view.startCardForm(
            cardFormWrapper,
            newCardMetadata.cardFormInitType
        )
        newCardMetadata.sheetOptions != null -> view.onNewCardWithSheetOptions()
        else -> Unit
    }
}
