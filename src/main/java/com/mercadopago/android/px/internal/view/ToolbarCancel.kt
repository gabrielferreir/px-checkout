package com.mercadopago.android.px.internal.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.mercadopago.android.px.R

internal class ToolbarCancel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    init {
        val systemAttrs = intArrayOf(android.R.attr.layout_height)
        val a = context.obtainStyledAttributes(attrs, systemAttrs)
        //Needs to be the same as height so icons are centered
        minimumHeight = a.getDimensionPixelSize(0, 0)
        val customAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.ToolbarCancel, 0, 0)
        val iconColor = customAttrs.getColor(R.styleable.ToolbarCancel_icon_color, Color.WHITE)
        customAttrs.recycle()
        a.recycle()
        setActionBar(iconColor)
    }

    private fun setActionBar(iconColor: Int) {
        val activity = getActivity()
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(this)
            activity.supportActionBar?.let {
                val actionBarIcon = ResourcesCompat.getDrawable(resources, R.drawable.px_ic_close, context.theme)
                actionBarIcon?.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP)
                it.setHomeAsUpIndicator(actionBarIcon)
                it.setHomeActionContentDescription(R.string.px_label_close)
                it.setDisplayHomeAsUpEnabled(true)
                it.setDisplayShowTitleEnabled(false)
            }
            setNavigationOnClickListener { activity.onBackPressed() }
        }
    }

    private fun getActivity(): Activity? {
        // Gross way of unwrapping the Activity so we can get the FragmentManager
        var context = context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }
}
