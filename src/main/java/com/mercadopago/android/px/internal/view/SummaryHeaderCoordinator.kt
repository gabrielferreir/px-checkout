package com.mercadopago.android.px.internal.view

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

internal class SummaryHeaderCoordinator(
    private val verticalHeaderDescriptor: ElementDescriptorView,
    private val horizontalHeaderDescriptor: ElementDescriptorView
) {
    private var currentHeaderDescriptor: ElementDescriptorView
    var shouldAnimateReturnFromCardForm: Boolean = false

    init {
        verticalHeaderDescriptor.isInvisible = true
        horizontalHeaderDescriptor.isVisible = true
        currentHeaderDescriptor = horizontalHeaderDescriptor
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        verticalHeaderDescriptor.setOnClickListener(listener)
        horizontalHeaderDescriptor.setOnClickListener(listener)
    }

    fun animateExit(duration: Long) {
        currentHeaderDescriptor.animateExit(if (showingVerticalHeader()) duration else null)
    }

    fun update(elementDescriptorModel: ElementDescriptorView.Model?) {
        if (elementDescriptorModel != null) {
            verticalHeaderDescriptor.update(elementDescriptorModel)
            horizontalHeaderDescriptor.update(elementDescriptorModel)
        } else {
            verticalHeaderDescriptor.isGone = true
            horizontalHeaderDescriptor.isGone = true
        }
    }

    /**
     * @param nextView the view to check if header is overlapping with
     */
    fun selectHeader(nextView: View) {
        val isOverlapping = isViewOverlapping(verticalHeaderDescriptor, nextView)
        if (isOverlapping && showingVerticalHeader()) {
            swapHeaderTo(horizontalHeaderDescriptor)
        } else if (!isOverlapping && !showingVerticalHeader()) {
            swapHeaderTo(verticalHeaderDescriptor)
            currentHeaderDescriptor.isVisible = true
        }
    }

    private fun swapHeaderTo(newHeader: ElementDescriptorView) {
        currentHeaderDescriptor.animateDisappear()
        currentHeaderDescriptor = newHeader
        currentHeaderDescriptor.animateAppear(shouldAnimateReturnFromCardForm)
        shouldAnimateReturnFromCardForm = false
    }

    private fun showingVerticalHeader() = currentHeaderDescriptor == verticalHeaderDescriptor

    private fun isViewOverlapping(firstView: View, secondView: View): Boolean {
        val yFirstViewEnd = firstView.top + firstView.height
        val ySecondViewInit = secondView.top
        return yFirstViewEnd >= ySecondViewInit
    }
}
