package com.mercadopago.android.px.internal.mappers

internal abstract class Mapper<T, V> {

    abstract fun map(value: T): V

    open fun map(values: Iterable<T>): List<V> {
        val returned = mutableListOf<V>()
        for (value in values) {
            returned.add(map(value))
        }
        return returned
    }
}
