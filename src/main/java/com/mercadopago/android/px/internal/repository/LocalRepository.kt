package com.mercadopago.android.px.internal.repository

internal interface LocalRepository<T> {
    val value: T
    fun configure(value: T)
    fun reset()
}