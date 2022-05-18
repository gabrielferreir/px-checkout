package com.mercadopago.android.px.internal.experiments

interface VariantHandler {
    @JvmDefault fun visit(variant: PulseVariant) = Unit
    @JvmDefault fun visit(variant: BadgeVariant) = Unit
    @JvmDefault fun visit(variant: ScrolledVariant) = Unit
}