package com.mercadopago.android.px.internal.features.one_tap.slider;

import android.view.View;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorModelByApplication;
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorView;
import com.mercadopago.android.px.internal.view.TitlePager;
import com.mercadopago.android.px.internal.viewmodel.GoingToModel;
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState;
import com.mercadopago.android.px.model.internal.Application;
import java.util.List;

import static com.mercadopago.android.px.internal.util.AccessibilityUtilsKt.executeIfAccessibilityTalkBackEnable;

public class TitlePagerAdapter extends HubableAdapter<List<PaymentMethodDescriptorModelByApplication>, TitlePager> {

    private static final int NO_SELECTED = -1;

    private PaymentMethodDescriptorView previousView;
    private PaymentMethodDescriptorView currentView;
    private PaymentMethodDescriptorView nextView;
    private int currentIndex = NO_SELECTED;

    public TitlePagerAdapter(@NonNull final TitlePager titlePager) {
        super(titlePager);
    }

    @Override
    public void updateData(final int currentIndex, final int payerCostSelected,
        @NonNull final SplitSelectionState splitSelectionState, @NonNull final Application application) {
        if (this.currentIndex != currentIndex) {
            final GoingToModel goingTo =
                this.currentIndex < currentIndex ? GoingToModel.BACKWARDS : GoingToModel.FORWARD;
            view.orderViews(goingTo);
            this.currentIndex = currentIndex;
        }
        data.get(currentIndex).update(application);
        refreshData(currentIndex, payerCostSelected, splitSelectionState);
    }

    @Override
    public void updatePosition(final float positionOffset, final int position) {
        final GoingToModel goingTo = position < currentIndex ? GoingToModel.BACKWARDS : GoingToModel.FORWARD;
        view.updatePosition(positionOffset, goingTo);
    }

    @Override
    public void updateViewsOrder(@NonNull final View previousView,
        @NonNull final View currentView,
        @NonNull final View nextView) {
        this.previousView = (PaymentMethodDescriptorView) previousView;
        this.currentView = (PaymentMethodDescriptorView) currentView;
        this.nextView = (PaymentMethodDescriptorView) nextView;
    }

    private void refreshData(final int currentIndex, final int payerCostSelected,
        @NonNull final SplitSelectionState splitSelectionState) {
        if (currentIndex > 0) {
            final PaymentMethodDescriptorView.Model previousModel = data.get(currentIndex - 1).getCurrent();
            previousView.update(previousModel);
        }

        final PaymentMethodDescriptorView.Model currentModel = data.get(currentIndex).getCurrent();

        currentModel.setCurrentPayerCost(payerCostSelected);
        currentModel.setSplit(splitSelectionState.userWantsToSplit());
        currentView.update(currentModel);
        executeIfAccessibilityTalkBackEnable(currentView.getContext(), () -> {
            currentView.updateContentDescription(currentModel);
            return null;
        });

        if (currentIndex + 1 < data.size()) {
            final PaymentMethodDescriptorView.Model nextModel = data.get(currentIndex + 1).getCurrent();
            nextView.update(nextModel);
        }
    }

    public Integer getCurrentInstallment() {
        final PaymentMethodDescriptorView.Model model = data != null ? data.get(currentIndex).getCurrent() : null;
        int installment = NO_SELECTED;

        if (model != null) {
            installment = model.getCurrentInstalment();
        }

        return installment;
    }

    @Override
    public List<PaymentMethodDescriptorModelByApplication> getNewModels(final HubAdapter.Model model) {
        return model.paymentMethodDescriptorModels;
    }
}