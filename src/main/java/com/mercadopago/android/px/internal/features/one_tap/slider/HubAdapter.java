package com.mercadopago.android.px.internal.features.one_tap.slider;

import android.view.View;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorModelByApplication;
import com.mercadopago.android.px.internal.viewmodel.ConfirmButtonViewModel;
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState;
import com.mercadopago.android.px.internal.viewmodel.SummaryModel;
import com.mercadopago.android.px.model.internal.Application;
import java.util.List;

public class HubAdapter extends ViewAdapter<HubAdapter.Model, View> {

    @NonNull private final List<? extends HubableAdapter> adapters;

    public static class Model {

        @NonNull public final List<PaymentMethodDescriptorModelByApplication> paymentMethodDescriptorModels;
        @NonNull public final List<SummaryModel> summaryViewModels;
        @NonNull public final List<SplitPaymentHeaderAdapter.Model> splitModels;
        @NonNull public final List<ConfirmButtonViewModel.ByApplication> confirmButtonViewModels;

        public Model(
            @NonNull final List<PaymentMethodDescriptorModelByApplication> paymentMethodDescriptorModels,
            @NonNull final List<SummaryModel> summaryViewModels,
            @NonNull final List<SplitPaymentHeaderAdapter.Model> splitModels,
            @NonNull final List<ConfirmButtonViewModel.ByApplication> confirmButtonViewModels
        ) {
            this.paymentMethodDescriptorModels = paymentMethodDescriptorModels;
            this.summaryViewModels = summaryViewModels;
            this.splitModels = splitModels;
            this.confirmButtonViewModels = confirmButtonViewModels;
        }
    }

    public HubAdapter(@NonNull final List<? extends HubableAdapter> adapters) {
        super(null);
        this.adapters = adapters;
    }

    @Override
    public void showInstallmentsList() {
        for (final HubableAdapter adapter : adapters) {
            adapter.showInstallmentsList();
        }
    }

    @Override
    public void updateData(final int currentIndex, final int payerCostSelected,
        @NonNull final SplitSelectionState splitSelectionState,
        @NonNull final Application application) {
        for (final HubableAdapter adapter : adapters) {
            adapter.updateData(currentIndex, payerCostSelected, splitSelectionState, application);
        }
    }

    @Override
    public void updatePosition(final float positionOffset, final int position) {
        for (final HubableAdapter adapter : adapters) {
            adapter.updatePosition(positionOffset, position);
        }
    }

    @Override
    public void updateViewsOrder(@NonNull final View previousView, @NonNull final View currentView,
        @NonNull final View nextView) {
        for (final HubableAdapter adapter : adapters) {
            adapter.updateViewsOrder(previousView, currentView, nextView);
        }
    }

    @Override
    public void update(@NonNull final Model newData) {
        super.update(newData);
        for (final HubableAdapter adapter : adapters) {
            //noinspection unchecked
            adapter.update(adapter.getNewModels(data));
        }
    }
}