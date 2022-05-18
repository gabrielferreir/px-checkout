package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.experiments.Variant;
import com.mercadopago.android.px.internal.viewmodel.GoingToModel;
import java.util.List;

public abstract class PaymentMethodHeaderView extends FrameLayout {

    /* default */ final ImageView helper;
    protected final TitlePager titlePager;
    protected boolean isDisabled;
    protected boolean hasBehaviour;
    protected String paymentType;
    protected boolean splitSelection;

    protected PaymentMethodHeaderView(@NonNull final Context context, @Nullable final AttributeSet attrs,
        final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
        titlePager = findViewById(R.id.title_pager);
        helper = findViewById(R.id.helper);
    }

    protected abstract void inflate();

    public interface Listener {
        void onDescriptorViewClicked();

        void onBehaviourDescriptorViewClick();

        void onDisabledDescriptorViewClick();

        void onInstallmentsSelectorCancelClicked();

        void onInstallmentViewUpdated();
    }

    public void updateData(final boolean hasPayerCost, final boolean isDisabled, final boolean hasBehaviour) {
        this.isDisabled = isDisabled;
        this.hasBehaviour = hasBehaviour;
    }

    public abstract void setListener(final Listener listener);

    public abstract void configureExperiment(@NonNull final List<Variant> variants);

    public abstract void showInstallmentsListTitle();

    public abstract void trackPagerPosition(float positionOffset, final Model model);

    public void setHelperVisibility(final boolean visible) {
        helper.setVisibility(visible ? VISIBLE : GONE);
    }

    public static class Model {
        final GoingToModel goingTo;
        final boolean currentIsExpandable;
        final boolean nextIsExpandable;

        public Model(final GoingToModel goingTo, final boolean currentIsExpandable,
            final boolean nextIsExpandable) {
            this.goingTo = goingTo;
            this.currentIsExpandable = currentIsExpandable;
            this.nextIsExpandable = nextIsExpandable;
        }
    }

    public void setPaymentTypeAndSplitSelection(final String paymentType, final boolean isSelected) {
        this.paymentType = paymentType;
        this.splitSelection = isSelected;
    }

    protected void setTitleVisibility(boolean isVisible) {
        titlePager.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

}