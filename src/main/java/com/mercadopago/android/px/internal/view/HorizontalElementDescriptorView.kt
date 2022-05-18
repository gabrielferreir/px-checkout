package com.mercadopago.android.px.internal.view

import android.content.Context
import android.util.AttributeSet
import android.view.accessibility.AccessibilityNodeInfo

internal class HorizontalElementDescriptorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ElementDescriptorView(context, attrs) {

    override fun animateAppear(shouldSlide: Boolean) {
        super.animateAppear(shouldSlide)
        post {
            performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null)
        }
    }
}
