package com.mercadopago.android.px.internal.extensions

import android.view.View
import androidx.core.view.ViewCompat
import com.mercadolibre.android.andesui.snackbar.AndesSnackbar
import com.mercadolibre.android.andesui.snackbar.action.AndesSnackbarAction
import com.mercadolibre.android.andesui.snackbar.duration.AndesSnackbarDuration
import com.mercadolibre.android.andesui.snackbar.type.AndesSnackbarType
import com.mercadopago.android.px.R

internal fun View.gone() = apply { visibility = View.GONE }

internal fun View.visible() = apply { visibility = View.VISIBLE }

internal fun View.invisible() = apply { visibility = View.INVISIBLE }

internal fun View?.addOnLaidOutListener(onLaidOut: ((view: View) -> Unit)) {
    this?.let {
        if (ViewCompat.isLaidOut(it)) {
            onLaidOut.invoke(it)
        }
        it.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ -> onLaidOut.invoke(view) }
    }
}

internal fun <T : View> T?.runWhenLaidOut(onLaidOut: ((view: T) -> Unit)) {
    this?.let {
        if (ViewCompat.isLaidOut(it)) {
            onLaidOut.invoke(it)
        } else {
            it.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                    oldTop: Int, oldRight: Int, oldBottom: Int) {
                    v?.apply { onLaidOut(it) }
                    it.removeOnLayoutChangeListener(this)
                }
            })
        }
    }
}

internal fun View?.setHeight(height: Int) {
    this?.let {
        val layout = it.layoutParams
        layout.height = height
        it.layoutParams = layout
    }
}

internal fun View?.showSnackBar(message: String = "",
    andesSnackbarType: AndesSnackbarType = AndesSnackbarType.ERROR,
    andesSnackbarDuration: AndesSnackbarDuration = AndesSnackbarDuration.LONG,
    andesSnackbarAction: AndesSnackbarAction? = null) {

    this?.let { view ->
        view.context?.let { context ->
            AndesSnackbar(context,
                view,
                andesSnackbarType,
                message.orIfEmpty(context.getString(R.string.px_error_title)),
                andesSnackbarDuration).also { it.action = andesSnackbarAction }.show()
        }
    }
}