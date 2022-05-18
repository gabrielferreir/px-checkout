package com.mercadopago.android.px.internal.util

import android.content.Context
import android.content.Context.ACCESSIBILITY_SERVICE
import android.view.accessibility.AccessibilityManager

typealias  Action = () -> Unit

inline fun executeIfAccessibilityTalkBackEnable(context: Context?, action: Action) {
    if (context.isAccessibilityTalkBackEnable()) {
        action.invoke()
    }
}

fun Context?.isAccessibilityTalkBackEnable(): Boolean {
    if (this == null) {
        return false
    }
    val manager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager?
    // Strictly interested in whether TalkBack is enabled
    return manager != null && manager.isEnabled && manager.isTouchExplorationEnabled
}