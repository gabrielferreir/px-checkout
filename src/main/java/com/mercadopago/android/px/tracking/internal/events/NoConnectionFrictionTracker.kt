package com.mercadopago.android.px.tracking.internal.events

import java.util.Locale

object NoConnectionFrictionTracker: FrictionEventTracker(
    "$BASE_PATH/${Id.NO_CONNECTION.name.toLowerCase(Locale.getDefault())}",
    Id.NO_CONNECTION,
    Style.SNACKBAR
)