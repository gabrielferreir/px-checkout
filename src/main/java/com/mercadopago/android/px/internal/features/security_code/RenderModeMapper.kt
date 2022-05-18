package com.mercadopago.android.px.internal.features.security_code

import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.internal.features.one_tap.RenderMode as RenderModeOneTap
import com.mercadopago.android.px.internal.features.security_code.RenderMode as RenderModeCVV

private const val HIGH_RES_MIN_HEIGHT = 620
private const val LOW_RES_MIN_HEIGHT = 585

internal class RenderModeMapper(private val availableHeight: Int, private val dynamicResMode: String) : Mapper<RenderModeOneTap, RenderModeCVV>() {

    override fun map(value: RenderModeOneTap): RenderModeCVV {
        return when (value) {
            RenderModeOneTap.HIGH_RES -> if (availableHeight >= HIGH_RES_MIN_HEIGHT) RenderModeCVV.HIGH_RES else RenderModeCVV.NO_CARD
            RenderModeOneTap.LOW_RES -> if (availableHeight >= LOW_RES_MIN_HEIGHT) RenderModeCVV.LOW_RES else RenderModeCVV.NO_CARD
            RenderModeOneTap.DYNAMIC -> if (availableHeight >= LOW_RES_MIN_HEIGHT) RenderModeCVV.from(dynamicResMode) else RenderModeCVV.NO_CARD
        }
    }
}