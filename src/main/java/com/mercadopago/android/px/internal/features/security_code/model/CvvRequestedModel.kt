package com.mercadopago.android.px.internal.features.security_code.model

import com.mercadopago.android.px.internal.features.one_tap.RenderMode

data class CvvRequestedModel(val fragmentContainer: Int = 0, val renderMode: RenderMode = RenderMode.DYNAMIC)
