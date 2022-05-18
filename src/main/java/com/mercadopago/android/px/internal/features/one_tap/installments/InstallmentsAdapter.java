package com.mercadopago.android.px.internal.features.one_tap.installments;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.model.PayerCost;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.android.px.model.PayerCost.NO_SELECTED;

public class InstallmentsAdapter extends RecyclerView.Adapter<InstallmentRowHolder> {

    @NonNull private List<InstallmentRowHolder.Model> models;
    private int payerCostSelected;
    @NonNull private final ItemListener itemListener;

    public interface ItemListener {
        void onClick(final PayerCost payerCostSelected);
    }

    public InstallmentsAdapter(@NonNull final ItemListener itemListener) {
        this.itemListener = itemListener;
        models = new ArrayList<>();
        payerCostSelected = NO_SELECTED;
    }

    public void setModels(@NonNull final List<InstallmentRowHolder.Model> models) {
        this.models = models;
    }

    public void setPayerCostSelected(final int payerCostSelected) {
        this.payerCostSelected = payerCostSelected;
    }

    @NonNull
    @Override
    public InstallmentRowHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View installmentView =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.px_view_payer_cost_item, parent, false);
        return new InstallmentRowHolder(installmentView);
    }

    @Override
    public void onBindViewHolder(@NonNull final InstallmentRowHolder holder, final int position) {
        holder.populate(itemListener, models.get(position));
        if (position == payerCostSelected) {
            holder.highLight();
        } else {
            holder.noHighLight();
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}