package com.mercadopago.android.px.internal.features.payment_result.components;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.features.payment_result.props.PaymentResultBodyProps;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultLegacyViewModel;
import com.mercadopago.android.px.internal.view.ActionDispatcher;

public final class PaymentResultLegacyRenderer {

    private PaymentResultLegacyRenderer() {
    }

    public static void render(@NonNull final ViewGroup parent, @NonNull final ActionDispatcher callback,
        @NonNull final PaymentResultLegacyViewModel viewModel, final boolean renderLegacyFooter, final boolean renderBody,
        @NonNull final PaymentResultViewModelFactory factory
    ) {
        final Body bodyComponent = getBodyComponent(factory, viewModel, callback);
        if (renderBody & bodyComponent.hasSomethingToDraw()) {
            parent.findViewById(R.id.body).setVisibility(View.GONE);
            bodyComponent.render(parent.findViewById(R.id.legacy_body));
        }

        if (renderLegacyFooter) {
            parent.addView(new FooterPaymentResult(factory, viewModel.model.getPaymentResult(), callback).render(parent));
        }
    }

    private static Body getBodyComponent(@NonNull final PaymentResultViewModelFactory factory,
        @NonNull final PaymentResultLegacyViewModel viewModel,
        @NonNull final ActionDispatcher callback) {
        final PaymentResultBodyProps bodyProps =
            new PaymentResultBodyProps.Builder(viewModel.configuration)
                .setPaymentResult(viewModel.model.getPaymentResult())
                .setCurrency(viewModel.model.getCurrency())
                .setPaymentResultMethodModels(viewModel.paymentResultMethodModels)
                .build();
        return new Body(factory, bodyProps, callback);
    }
}
