package com.mercadopago.android.px

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.junit.Assert
import org.robolectric.util.ReflectionHelpers

/**
 * Extensions
 */
internal fun View.assertVisible() {
    Assert.assertEquals(visibility, View.VISIBLE)
}

internal fun View.assertVisible(viewId: Int) {
    findView<View>(viewId).assertVisible()
}

internal fun View.assertGone() {
    Assert.assertEquals(visibility, View.GONE)
}

internal fun View.assertInvisible() = Assert.assertEquals(visibility, View.INVISIBLE)

internal fun View.assertGone(viewId: Int) {
    findView<View>(viewId).assertGone()
}

internal inline fun <reified V : View> ViewGroup.assertChildCount(expected: Int) {
    var actual = 0
    for (i in 0..childCount) {
        if (getChildAt(i) is V) {
            actual++
        }
    }
    Assert.assertEquals(actual, expected)
}

internal fun TextView.assertText(text: CharSequence) {
    Assert.assertEquals(text.toString(), this.text.toString())
}

internal inline fun <reified T> Any.getField(field: String): T = ReflectionHelpers.getField(this, field)

internal fun Any.setField(field: String, value: Any) = ReflectionHelpers.setField(this, field, value)

internal inline fun <reified V : View> View.findView(viewId: Int): V = findViewById(viewId)

internal fun <T : Any> T.assertEquals(expected: T) = Assert.assertEquals(expected, this)
