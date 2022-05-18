package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.presentation.extensions.ViewExtKt;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.features.payment_result.props.BodyErrorProps;
import com.mercadopago.android.px.internal.util.ListUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.view.PaymentResultMethod;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultViewModel;
import java.util.List;
import kotlin.Unit;

public class BodyError extends CompactComponent<BodyErrorProps, ActionDispatcher> {

    @NonNull private final PaymentResultViewModel paymentResultViewModel;

    public BodyError(@NonNull final PaymentResultViewModelFactory factory,
        @NonNull final BodyErrorProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
        paymentResultViewModel = factory.createPaymentStatusWithProps(props);
    }

    public String getTitle(final Context context) {
        return paymentResultViewModel.getBodyTitle(context);
    }

    public String getDescription(final Context context) {
        return paymentResultViewModel.getDescription(context);
    }

    private String getTitleDescription(final Context context) {
        return paymentResultViewModel.getTitleDescription(context);
    }

    private void renderMethods(final View view, final List<PaymentResultMethod.Model> methodModels) {
        final PaymentResultMethod primaryMethod = view.findViewById(R.id.primaryMethod);
        final PaymentResultMethod secondaryMethod = view.findViewById(R.id.secondaryMethod);

        final boolean shouldShowPaymentMethods = paymentResultViewModel.shouldShowPaymentMethods() &&
            ListUtil.isNotEmpty(methodModels);

        ViewExtKt.loadOrGone(primaryMethod, shouldShowPaymentMethods, () -> {
            primaryMethod.setModel(methodModels.get(0));
            return Unit.INSTANCE;
        });

        ViewExtKt.loadOrGone(secondaryMethod, shouldShowPaymentMethods && methodModels.size() > 1, () -> {
            secondaryMethod.setModel(methodModels.get(1));
            return Unit.INSTANCE;
        });
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View bodyErrorView = LayoutInflater.from(context).inflate(R.layout.px_payment_result_body_error, parent);
        final ViewGroup bodyViewGroup = bodyErrorView.findViewById(R.id.bodyErrorContainer);
        final MPTextView titleTextView = bodyViewGroup.findViewById(R.id.help_title);
        final MPTextView descriptionTextView = bodyViewGroup.findViewById(R.id.help_description);
        final MPTextView titleDescriptionTextView =
            bodyViewGroup.findViewById(R.id.paymentResultBodyErrorTitleDescription);
        final View bodyErrorDescriptionDivider = bodyViewGroup.findViewById(R.id.bodyErrorDescriptionDivider);

        ViewUtils.loadOrGone(getTitle(context), titleTextView);
        ViewUtils.loadOrGone(getTitleDescription(context), titleDescriptionTextView);
        ViewUtils.loadOrGone(getDescription(context), descriptionTextView);

        if (getTitle(context).isEmpty()) {
            final LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            final int marginTop = (int) context.getResources().getDimension(R.dimen.px_l_margin);
            params.setMargins(0, marginTop, 0, 0);
            descriptionTextView.setLayoutParams(params);
        }

        if (!getTitleDescription(context).isEmpty()) {
            bodyErrorDescriptionDivider.setVisibility(View.VISIBLE);
        }

        renderMethods(bodyErrorView, props.paymentResultMethodModels);

        return bodyErrorView;
    }
}
