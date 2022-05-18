package com.mercadopago.android.px.internal.util

import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Locale

private const val SHA_1 = "SHA-1"
private const val HEX_FORMAT = "%02x"

internal fun encryptToSha1(code: String): String? {
    return runCatching {
        val messageDigest = MessageDigest.getInstance(SHA_1)
        messageDigest.update(code.toByteArray(StandardCharsets.UTF_8))
        byteArrayToHexString(messageDigest.digest())
    }.getOrNull()
}

private fun byteArrayToHexString(bytes: ByteArray): String {
    val buffer = StringBuilder()
    for (b in bytes) {
        buffer.append(String.format(Locale.getDefault(), HEX_FORMAT, b))
    }
    return buffer.toString()
}
