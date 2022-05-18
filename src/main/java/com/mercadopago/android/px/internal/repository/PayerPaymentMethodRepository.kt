package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.CustomSearchItem

internal interface PayerPaymentMethodRepository : LocalRepository<List<@kotlin.jvm.JvmSuppressWildcards CustomSearchItem>> {
    operator fun get(key: PayerPaymentMethodKey): CustomSearchItem?
    operator fun get(customOptionId: String): CustomSearchItem?
    fun getIdsWithSplitAllowed(): Set<String>
}