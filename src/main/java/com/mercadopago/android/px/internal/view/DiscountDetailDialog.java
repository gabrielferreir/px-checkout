package com.mercadopago.android.px.internal.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import com.mercadolibre.android.ui.widgets.MeliDialog;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.mappers.DiscountConfigurationMapper;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.tracking.internal.views.AppliedDiscountViewTracker;

public class DiscountDetailDialog extends MeliDialog {

    private static final String TAG = DiscountDetailDialog.class.getName();
    private static final String ARG_DISCOUNT = "arg_discount";

    public static void showDialog(@NonNull final FragmentManager supportFragmentManager, @NonNull final DiscountConfigurationModel discountModel) {
        final DiscountDetailDialog discountDetailDialog = new DiscountDetailDialog();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_DISCOUNT, discountModel);
        discountDetailDialog.setArguments(arguments);
        discountDetailDialog.show(supportFragmentManager, TAG);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ViewGroup container = view.findViewById(R.id.main_container);
        final Bundle arguments =  getArguments();
        final DiscountConfigurationModel discountModel;
        if (arguments != null && (discountModel = arguments.getParcelable(ARG_DISCOUNT)) != null) {
            Session.getInstance().getTracker().track(new AppliedDiscountViewTracker(discountModel));
            final DiscountDetailContainer discountDetailContainer = new DiscountDetailContainer(
                DiscountConfigurationMapper.INSTANCE.map(discountModel.getDiscountDescription()));

            discountDetailContainer.render(container);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.px_dialog_detail_discount;
    }
}
