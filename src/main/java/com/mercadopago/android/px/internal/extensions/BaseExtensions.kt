package com.mercadopago.android.px.internal.extensions

import android.app.Activity
import android.graphics.Rect
import androidx.fragment.app.Fragment
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@UseExperimental(ExperimentalContracts::class)
internal fun CharSequence?.isNotNullNorEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullNorEmpty != null)
    }

    return !isNullOrEmpty()
}

@UseExperimental(ExperimentalContracts::class)
internal fun Any?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }

    return this != null
}

internal fun <T : CharSequence> T?.orIfEmpty(fallback: T) = if (isNotNullNorEmpty()) this!! else fallback

internal fun Any?.runIfNull(action: () -> Unit) {
    if (this == null) {
        action.invoke()
    }
}

internal inline fun <reified T: Any> notNull(param: T?) = checkNotNull(
    param,
    {"${T::class.java.simpleName} should not be null"}
)

internal inline fun <T : Any?, R> T?.runIfNotNull(action: (T) -> R): R? = this?.run { action(this) }

internal fun <T : CharSequence> T?.runIfNotNullNorEmpty(action: (T) -> Unit): Boolean {
    if (isNotNullNorEmpty()) {
        action.invoke(this!!)
        return true
    }
    return false
}

internal fun Fragment.postDelayed(delay: Long, runnable: (() -> Unit)) = view?.postDelayed(runnable, delay)

internal fun Activity.addKeyBoardListener(
    onKeyBoardOpen: (() -> Unit)? = null,
    onKeyBoardClose: (() -> Unit)? = null
) {
    window.decorView.rootView?.apply {
        viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()

            getWindowVisibleDisplayFrame(r)

            val heightDiff = rootView.height - (r.bottom - r.top)
            if (heightDiff > rootView.height * 0.15) {
                onKeyBoardOpen?.invoke()
            } else {
                onKeyBoardClose?.invoke()
            }
        }
    }
}