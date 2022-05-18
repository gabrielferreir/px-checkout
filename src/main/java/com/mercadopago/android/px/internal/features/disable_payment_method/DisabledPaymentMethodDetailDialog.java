package com.mercadopago.android.px.internal.features.disable_payment_method;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.mercadolibre.android.ui.widgets.MeliButton;
import com.mercadolibre.android.ui.widgets.MeliDialog;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.StatusMetadata;
import com.mercadopago.android.px.tracking.internal.views.DisabledPaymentMethodDetailViewTracker;

public class DisabledPaymentMethodDetailDialog extends MeliDialog {

    private static final String TAG = DisabledPaymentMethodDetailDialog.class.getName();
    private static final String ARG_DISABLED_PAYMENT_METHOD = "arg_disabled_payment_method";
    private static final String ARG_STATUS_METADATA = "arg_status_metadata";

    @Nullable private StatusMetadata status;
    private String statusDetail;

    @SuppressWarnings("TypeMayBeWeakened")
    public static void showDialog(@NonNull final Fragment targetFragment, final int requestCode,
        @Nullable final String statusDetail, @Nullable final StatusMetadata status) {
        final FragmentActivity activity = targetFragment.getActivity();
        if (activity != null) {
            final DisabledPaymentMethodDetailDialog instance = new DisabledPaymentMethodDetailDialog();
            final Bundle arguments = new Bundle();
            arguments.putString(ARG_DISABLED_PAYMENT_METHOD, statusDetail);
            arguments.putParcelable(ARG_STATUS_METADATA, status);
            instance.setArguments(arguments);
            instance.show(activity.getSupportFragmentManager(), TAG);
            instance.setTargetFragment(targetFragment, requestCode);
        }
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        final Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_DISABLED_PAYMENT_METHOD) &&
            arguments.containsKey(ARG_STATUS_METADATA)) {
            //noinspection ConstantConditions
            statusDetail = getArguments().getString(ARG_DISABLED_PAYMENT_METHOD);
            status = getArguments().getParcelable(ARG_STATUS_METADATA);
        } else {
            throw new IllegalStateException(getClass().getSimpleName() + " does not contain model info");
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View closeButton = view.findViewById(R.id.ui_melidialog_close_button);
        closeButton.setOnClickListener(v -> dismiss());
        final TextView content = view.findViewById(R.id.px_dialog_detail_payment_method_disable_content);

        Session.getInstance().getTracker().track(new DisabledPaymentMethodDetailViewTracker());

        content.setText(status != null && !status.isEnabled() ?
            status.getSecondaryMessage().getMessage() : getContent(statusDetail));

        final MeliButton linkText = view.findViewById(R.id.px_dialog_detail_payment_method_disable_link);

        linkText.setText(
            status.getLabel() != null ?
                status.getLabel().getMessage() :
                getString(R.string.px_text_pay_with_other_method)
        );
        linkText.setOnClickListener(v -> finish());
    }

    public void finish() {
        dismiss();
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.px_dialog_detail_payment_method_disable;
    }

    private String getContent(@Nullable final String statusDetail) {
        final int resId;
        switch (statusDetail != null ? statusDetail : TextUtil.EMPTY) {
        case Payment.StatusDetail
            .STATUS_DETAIL_REJECTED_HIGH_RISK:
        case Payment.StatusDetail
            .STATUS_DETAIL_CC_REJECTED_HIGH_RISK:
            resId = R.string.px_dialog_detail_payment_method_disable_high_risk;
            break;
        case Payment.StatusDetail
            .STATUS_DETAIL_CC_REJECTED_BLACKLIST:
            resId = R.string.px_dialog_detail_payment_method_disable_black_list;
            break;
        case Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT:
            resId = R.string.px_dialog_detail_payment_method_disable_insufficient_amount;
            break;
        default:
            resId = 0;
        }

        //noinspection ConstantConditions
        return getContext().getString(resId) + (resId != 0 ? TextUtil.NL + TextUtil.NL : TextUtil.EMPTY) +
            getContext().getString(R.string.px_text_try_with_other_method);
    }
}
