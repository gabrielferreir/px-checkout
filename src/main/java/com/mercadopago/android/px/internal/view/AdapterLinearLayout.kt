package com.mercadopago.android.px.internal.view

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.widget.Adapter
import androidx.appcompat.widget.LinearLayoutCompat
import com.mercadopago.android.px.R

internal class AdapterLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private var adapter: Adapter? = null
    private val spaceBetweenChildren: Int

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AdapterLinearLayout)
        spaceBetweenChildren = a.getDimensionPixelSize(R.styleable.AdapterLinearLayout_spaceBetweenChildren, 0)
        a.recycle()
    }

    private val dataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            reloadChildViews()
        }
    }

    fun setAdapter(adapter: Adapter?) {
        if (this.adapter === adapter) {
            return
        }
        this.adapter = adapter
        adapter?.registerDataSetObserver(dataSetObserver)
        reloadChildViews()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapter?.unregisterDataSetObserver(dataSetObserver)
    }

    private fun reloadChildViews() {
        removeAllViews()
        adapter?.let { adapter ->
            for (position in 0 until adapter.count) {
                adapter.getView(position, null, this)?.let { view ->
                    addView(view)
                    view.takeUnless { position == adapter.count }?.let {
                        val layoutParams = it.layoutParams as LayoutParams
                        layoutParams.bottomMargin += spaceBetweenChildren
                        it.layoutParams = layoutParams
                    }
                }
            }
        }
        requestLayout()
    }
}
