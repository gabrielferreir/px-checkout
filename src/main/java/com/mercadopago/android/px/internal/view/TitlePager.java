package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.experiments.Variant;
import com.mercadopago.android.px.internal.features.one_tap.slider.ViewAdapter;
import com.mercadopago.android.px.internal.viewmodel.GoingToModel;

public abstract class TitlePager extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    protected View previousView;
    protected View currentView;
    protected View nextView;
    private int currentWidth;
    private ViewAdapter adapter;

    public TitlePager(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
    }

    protected abstract void inflate();

    public final void setBadgeExperimentVariant(@NonNull final Variant variant) {
        ((PaymentMethodDescriptorView) previousView).configureExperiment(variant);
        ((PaymentMethodDescriptorView) currentView).configureExperiment(variant);
        ((PaymentMethodDescriptorView) nextView).configureExperiment(variant);
    }

    @Override
    protected void onFinishInflate() {
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        super.onFinishInflate();
    }

    @Override
    public void onGlobalLayout() {
        if (getWidth() > 0) {
            currentWidth = getWidth();
            resetViewsPosition();
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    public final void setAdapter(final ViewAdapter adapter) {
        this.adapter = adapter;
    }

    public final void updatePosition(final float offset, final GoingToModel goingTo) {
        float positionOffset = offset;

        if (positionOffset == 0.0f) {
            return;
        }

        if (goingTo == GoingToModel.BACKWARDS) {
            previousView.setAlpha(1.0f - positionOffset);
            currentView.setAlpha(positionOffset);
            positionOffset = 1.0f - positionOffset;
            positionOffset *= -1.0f;
        } else {
            //Added to fix lint error, not really needed
            positionOffset = Math.abs(positionOffset);
            nextView.setAlpha(positionOffset);
            currentView.setAlpha(1.0f - positionOffset);
        }

        final float offsetInPixels = currentWidth * positionOffset;

        previousView.setX(-currentWidth - offsetInPixels);
        currentView.setX(-offsetInPixels);
        nextView.setX(currentWidth - offsetInPixels);
    }

    public final void orderViews(final GoingToModel goingTo) {
        final View auxView;
        if (goingTo == GoingToModel.BACKWARDS) {
            auxView = previousView;
            previousView = currentView;
            currentView = nextView;
            nextView = auxView;
        } else {
            auxView = nextView;
            nextView = currentView;
            currentView = previousView;
            previousView = auxView;
        }
        resetViewsPosition();
        adapter.updateViewsOrder(previousView, currentView, nextView);
    }

    private void resetViewsPosition() {
        currentView.setX(0);
        previousView.setX(-currentWidth);
        nextView.setX(currentWidth);
    }
}
