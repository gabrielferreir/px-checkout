package com.mercadopago.android.px.internal.features.one_tap.installments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;

public class InstallmentsAdapterV2 extends InstallmentsAdapter {

    public InstallmentsAdapterV2(@NonNull final ItemListener itemListener) {
        super(itemListener);
    }

    @NonNull
    @Override
    public InstallmentRowHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View installmentView =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.px_view_payer_cost_item_v2, parent, false);
        return new InstallmentRowHolderV2(installmentView);
    }
}