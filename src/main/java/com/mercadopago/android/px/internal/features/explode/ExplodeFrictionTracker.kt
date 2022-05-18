package com.mercadopago.android.px.internal.features.explode

import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker

object ExplodeFrictionTracker :
    FrictionEventTracker("$BASE_PATH/explode", Id.GENERIC, Style.CUSTOM_COMPONENT)