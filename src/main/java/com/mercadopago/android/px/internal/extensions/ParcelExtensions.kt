package com.mercadopago.android.px.internal.extensions

import android.os.Parcel
import java.math.BigDecimal
import java.util.*

internal fun Parcel.writeDate(date: Date?) = writeLong(date?.time ?: -1L)

internal fun Parcel.readDate(): Date? {
    val long = readLong()
    return if (long != -1L) Date(long) else null
}

internal fun Parcel.writeBigDecimal(value: BigDecimal?) = value?.let {
    writeByte(1.toByte())
    writeString(it.toString())
} ?: writeByte(0.toByte())

internal fun Parcel.readBigDecimal() = if (readByte().toInt() == 0) null else BigDecimal(readString())

internal fun Parcel.writeNullableInt(value: Int?) = value?.let {
    writeByte(1.toByte())
    writeInt(it)
} ?: writeByte(0.toByte())

internal fun Parcel.readNullableInt() = if (readByte().toInt() == 0) null else readInt()