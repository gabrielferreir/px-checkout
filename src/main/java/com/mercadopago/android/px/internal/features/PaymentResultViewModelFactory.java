package com.mercadopago.android.px.internal.features;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.actions.ChangePaymentMethodAction;
import com.mercadopago.android.px.internal.actions.NextAction;
import com.mercadopago.android.px.internal.actions.RecoverPaymentAction;
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultDecorator;
import com.mercadopago.android.px.internal.features.payment_result.props.BodyErrorProps;
import com.mercadopago.android.px.internal.util.StatusHelper;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker;
import java.util.HashMap;
import java.util.Map;

import static com.mercadopago.android.px.model.Payment.StatusCodes.STATUS_APPROVED;
import static com.mercadopago.android.px.model.Payment.StatusCodes.STATUS_IN_PROCESS;
import static com.mercadopago.android.px.model.Payment.StatusCodes.STATUS_PENDING;
import static com.mercadopago.android.px.model.Payment.StatusCodes.STATUS_REJECTED;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BLACKLIST;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_FRAUD;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_HIGH_RISK;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_PENDING_PROVIDER_RESPONSE;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_BY_REGULATIONS;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_CAP_EXCEEDED;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_INSUFFICIENT_AMOUNT;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK;
import static com.mercadopago.android.px.model.Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA;

public final class PaymentResultViewModelFactory {

    private static final int EMPTY_LABEL = 0;

    @NonNull private final MPTracker tracker;

    public PaymentResultViewModelFactory(@NonNull final MPTracker tracker) {
        this.tracker = tracker;
    }

    public PaymentResultDecorator createPaymentResultDecorator(@NonNull final PaymentResult paymentResult) {
        final PaymentResultViewModel vm =
            createPaymentResultViewModel(paymentResult.getPaymentStatus(), paymentResult.getPaymentStatusDetail());
        return PaymentResultDecorator.from(vm);
    }

    public PaymentResultViewModel createPaymentResultViewModel(@NonNull final String statusCode,
        @NonNull final String statusDetail) {
        return createPaymentResultViewModel(generatePaymentResult(statusCode, statusDetail));
    }

    private PaymentResult generatePaymentResult(@NonNull final String statusCode,
        @NonNull final String statusDetail) {
        return new PaymentResult.Builder()
            .setPaymentStatus(statusCode)
            .setPaymentStatusDetail(statusDetail)
            .build();
    }

    public PaymentResultViewModel createPaymentResultViewModel(@NonNull final PaymentResult paymentResult) {
        return createViewModelBuilder(paymentResult, null).build();
    }

    /**
     * We need payment information in order to return the correct description
     *
     * @param props body information
     */
    public PaymentResultViewModel createPaymentStatusWithProps(@Nullable final BodyErrorProps props) {
        return createViewModelBuilder(generatePaymentResult(props.status, props.statusDetail), props).build();
    }

    @SuppressWarnings("fallthrough")
    private PaymentResultViewModel.Builder createViewModelBuilder(@NonNull final PaymentResult paymentResult,
        @Nullable final BodyErrorProps props) {

        final String status = paymentResult.getPaymentStatus();
        final String detail = paymentResult.getPaymentStatusDetail();
        final String paymentMethodName = props == null ? TextUtil.EMPTY : props.paymentMethodName;
        final String paymentAmount = props == null ? null : props.paymentAmount;

        final PaymentResultViewModel.Builder builder = new PaymentResultViewModel.Builder();
        // defaults
        builder.setLinkAction(new NextAction());
        builder.setLinkActionTitle(R.string.px_button_continue);
        setApprovedResources(builder);

        switch (status) {
        case STATUS_APPROVED:
            return builder
                .setTitleResId(R.string.px_title_approved_payment)
                .setApprovedSuccess(true);
        // Fallthrough pending & in process
        case STATUS_PENDING:
            builder.setLinkAction(new NextAction());
        case STATUS_IN_PROCESS:
            return inProcessStatusBuilder(detail, status, builder);

        case STATUS_REJECTED:
            setRecoverableErrorResources(builder);
            // defaults
            builder.setMainAction(new ChangePaymentMethodAction());
            builder.setIsErrorRecoverable(true);
            builder.setMainActionTitle(R.string.px_change_payment_method);
            builder.setHasDetail(true);
            return rejectedStatusBuilder(detail, builder, paymentMethodName, paymentAmount);

        default:
            builder.setHasDetail(true);
            return unknownStatusFallback(builder, status, detail);
        }
    }

    /**
     * Generate friction event when we receive an unknown status detail Payment information might be useful ToDo Add
     * payment information that might be useful
     */
    private void generateFrictionEvent(final String statusCode, final String statusDetail) {

        final Map<String, String> metadata = new HashMap<>();
        // Add metadata values
        metadata.put("status_received", statusCode);
        metadata.put("status_detail", statusDetail);

        tracker.track(FrictionEventTracker.with(
            "/px_checkout/result",
            FrictionEventTracker.Id.INVALID_STATUS_DETAIL,
            FrictionEventTracker.Style.NON_SCREEN,
            metadata));
    }

    private int checkPaymentMethodsOff(final String status, final String detail) {
        if (status.equalsIgnoreCase(STATUS_PENDING) && StatusHelper.isPendingStatusDetailSuccess(detail)) {
            return EMPTY_LABEL;
        } else {
            return R.string.px_title_pending_payment;
        }
    }

    private int getPendingDescription(final String detail) {
        switch (detail) {
        case Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY:
            return R.string.px_error_description_contingency;
        case Payment.StatusDetail.STATUS_DETAIL_PENDING_REVIEW_MANUAL:
            return R.string.px_error_description_review_manual;
        default:
            return EMPTY_LABEL;
        }
    }

    private PaymentResultViewModel.Builder inProcessStatusBuilder(
        final String detail,
        final String status,
        final PaymentResultViewModel.Builder builder
    ) {
        setPendingResources(builder, detail);

        if (STATUS_DETAIL_PENDING_PROVIDER_RESPONSE.equals(detail)) {
            return builder
                .setIconResId(R.drawable.px_ic_bank_transfer_error)
                .setTitleResId(R.string.px_title_pending_payment)
                .setBodyDetailDescriptionResId(R.string.px_pending_body_detail_bank_transfer)
                .setLinkAction(new NextAction())
                .setPendingWarning(true)
                .setHasDetail(true)
                .setShowPaymentMethods(true)
                .setLinkActionTitle(R.string.px_button_text_go_to_home);
        }

        return builder
            .setTitleResId(checkPaymentMethodsOff(status, detail))
            .setDescriptionResId(getPendingDescription(detail))
            .setLinkActionTitle(R.string.px_got_it)
            .setApprovedSuccess(StatusHelper.isPendingStatusDetailSuccess(detail))
            .setPendingSuccess(StatusHelper.isPendingStatusDetailSuccess(detail))
            .setPendingWarning(!StatusHelper.isPendingStatusDetailSuccess(detail))
            .setHasDetail(true);
    }

    private PaymentResultViewModel.Builder rejectedStatusBuilder(final String detail,
        final PaymentResultViewModel.Builder builder, final String paymentMethodName,
        final String paymentAmount) {

        if (!Payment.StatusDetail.isKnownStatusDetail(detail)) {
            return unknownStatusFallback(builder, STATUS_REJECTED, detail);
        }

        switch (detail) {

        case STATUS_DETAIL_CC_REJECTED_PLUGIN_PM:
        case STATUS_DETAIL_CC_REJECTED_OTHER_REASON:
            setNonRecoverableErrorResources(builder);
            return builder
                .setTitleResId(R.string.px_title_other_reason_rejection)
                .setLinkAction(null)
                .setMainAction(new ChangePaymentMethodAction())
                .setMainActionTitle(R.string.px_change_payment_method);
        case STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT:
            setNonRecoverableErrorResources(builder);
            return builder
                .setTitleResId(R.string.px_title_duplicated_reason_rejection)
                .setMainAction(null)
                .setLinkAction(new NextAction())
                .setDescriptionResId(R.string.px_error_description_duplicated_payment)
                .setLinkActionTitle(R.string.px_got_it);
        case STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT:
            setRecoverableErrorResources(builder);
            return builder
                .setTitleResId(R.string.px_text_insufficient_amount)
                .setLinkAction(null)
                .setMainActionTitle(R.string.px_change_payment_method)
                .setBodyTitleResId(R.string.px_what_can_do)
                .setBodyDetailDescriptionResId(R.string.px_text_insufficient_amount_title_description)
                .setDescriptionResId(R.string.px_error_description_rejected_by_insufficient_amount_1)
                .setSecondDescriptionResId(R.string.px_error_description_rejected_by_insufficient_amount_2);
        case STATUS_DETAIL_CC_REJECTED_CARD_DISABLED:
            setRecoverableErrorResources(builder);
            return builder
                .setTitleResId(R.string.px_text_active_card)
                .setMainAction(new RecoverPaymentAction())
                .setMainActionTitle(R.string.px_text_card_enabled)
                .setLinkAction(new ChangePaymentMethodAction())
                .setLinkActionTitle(R.string.px_text_pay_with_other_method)
                .setDescriptionResId(R.string.px_error_description_card_disabled, paymentMethodName)
                .setBodyTitleResId(R.string.px_what_can_do);
        case STATUS_DETAIL_CC_REJECTED_HIGH_RISK:
            return getHighRiskBuilder(builder, R.string.px_title_rejection_high_risk);
        case STATUS_DETAIL_REJECTED_HIGH_RISK:
            setNonRecoverableErrorResources(builder);
            return builder
                .setBadgeResId(0)
                .setIconResId(R.drawable.px_ic_badge_error)
                .setTitleResId(R.string.px_title_error_rejected_high_risk)
                .setBodyDetailDescriptionResId(R.string.px_body_error_rejected_high_risk)
                .setMainAction(new ChangePaymentMethodAction())
                .setMainActionTitle(R.string.px_change_payment_method)
                .setLinkAction(new NextAction())
                .setLinkActionTitle(R.string.px_button_text_go_to_home);
        case STATUS_DETAIL_REJECTED_BY_REGULATIONS:
            setNonRecoverableErrorResources(builder);
            return builder
                .setTitleResId(R.string.px_title_other_reason_rejection)
                .setMainAction(new ChangePaymentMethodAction())
                .setMainActionTitle(R.string.px_change_payment_method)
                .setLinkAction(null);
        case STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS:
            setNonRecoverableErrorResources(builder);
            return builder
                .setMainAction(new ChangePaymentMethodAction())
                .setMainActionTitle(R.string.px_change_payment_method)
                .setTitleResId(R.string.px_title_rejection_max_attempts)
                .setBodyTitleResId(R.string.px_what_can_do)
                .setDescriptionResId(R.string.px_error_description_max_attempts)
                .setLinkAction(null);
        case STATUS_DETAIL_CC_REJECTED_BLACKLIST:
            setNonRecoverableErrorResources(builder);
            return builder
                .setLinkAction(null)
                .setMainActionTitle(R.string.px_change_payment_method)
                .setTitleResId(R.string.px_title_rejection_blacklist);
        case STATUS_DETAIL_CC_REJECTED_FRAUD:
            setNonRecoverableErrorResources(builder);
            return builder
                .setLinkAction(null)
                .setMainActionTitle(R.string.px_button_continue)
                .setMainAction(new NextAction())
                .setTitleResId(R.string.px_title_rejection_fraud);
        case STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE:
            return builder
                .setTitleResId(R.string.px_title_activity_call_for_authorize)
                .setMainAction(new RecoverPaymentAction())
                .setMainActionTitle(R.string.px_text_authorized_call_for_authorize)
                .setLinkAction(new ChangePaymentMethodAction())
                .setLinkActionTitle(R.string.px_text_pay_with_other_method)
                .setBodyTitleResId(R.string.px_text_how_can_authorize)
                .setDescriptionResId(R.string.px_error_description_call_1, paymentAmount)
                .setSecondDescriptionResId(R.string.px_error_description_call_2);

        case STATUS_DETAIL_REJECTED_REJECTED_BY_BANK:
        case STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA:
            setNonRecoverableErrorResources(builder);
            return builder
                .setTitleResId(R.string.px_bolbradesco_rejection)
                .setBodyTitleResId(R.string.px_what_can_do)
                .setDescriptionResId(R.string.px_error_try_with_other_method);

        case STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER:
        case STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER:
        case STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE:
        case STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE:
            return builder
                .setTitleResId(R.string.px_text_some_card_data_is_incorrect)
                .setMainAction(new RecoverPaymentAction())
                .setMainActionTitle(R.string.px_error_bad_filled_action)
                .setLinkAction(new ChangePaymentMethodAction())
                .setLinkActionTitle(R.string.px_text_pay_with_other_method);

        case STATUS_DETAIL_REJECTED_INSUFFICIENT_AMOUNT:
            setNonRecoverableErrorResources(builder);
            return builder
                .setBadgeResId(0)
                .setIconResId(R.drawable.px_ic_badge_error)
                .setTitleResId(R.string.px_title_error_rejected_insufficient_amount)
                .setBodyDetailDescriptionResId(R.string.px_body_error_rejected_insufficient_amount)
                .setMainAction(new ChangePaymentMethodAction())
                .setMainActionTitle(R.string.px_change_payment_method)
                .setLinkAction(new NextAction())
                .setLinkActionTitle(R.string.px_button_text_go_to_home);

        case STATUS_DETAIL_REJECTED_CAP_EXCEEDED:
            setNonRecoverableErrorResources(builder);
            return builder
                .setBadgeResId(0)
                .setIconResId(R.drawable.px_ic_badge_error)
                .setTitleResId(R.string.px_title_error_rejected_cap_exceeded)
                .setBodyDetailDescriptionResId(R.string.px_body_error_rejected_cap_exceeded)
                .setMainAction(new ChangePaymentMethodAction())
                .setMainActionTitle(R.string.px_change_payment_method)
                .setLinkAction(new NextAction())
                .setLinkActionTitle(R.string.px_button_text_go_to_home);

        default:
            setNonRecoverableErrorResources(builder);
            return builder
                .setBadgeResId(0)
                .setIconResId(R.drawable.px_ic_badge_error)
                .setTitleResId(R.string.px_title_error_rejected_default)
                .setBodyDetailDescriptionResId(R.string.px_body_error_rejected_default)
                .setLinkAction(new NextAction())
                .setLinkActionTitle(R.string.px_button_text_go_to_home)
                .setPendingSuccess(false)
                .setPendingWarning(false);
        }
    }

    private PaymentResultViewModel.Builder getHighRiskBuilder(final PaymentResultViewModel.Builder builder,
        final int resId) {
        setNonRecoverableErrorResources(builder);
        return builder
            .setTitleResId(resId)
            .setLinkAction(null)
            .setBodyTitleResId(R.string.px_what_can_do)
            .setDescriptionResId(R.string.px_text_try_with_other_method)
            .setMainAction(new ChangePaymentMethodAction())
            .setMainActionTitle(R.string.px_change_payment_method)
            .setHasDetail(true);
    }

    private void setApprovedResources(final PaymentResultViewModel.Builder builder) {
        builder
            .setBackgroundColor(R.color.ui_components_success_color)
            .setBadgeResId(R.drawable.px_badge_check);
    }

    private void setNonRecoverableErrorResources(@NonNull final PaymentResultViewModel.Builder builder) {
        builder
            .setIsErrorRecoverable(false)
            .setBadgeResId(R.drawable.px_badge_error)
            .setBackgroundColor(R.color.ui_components_error_color);
    }

    private void setRecoverableErrorResources(@NonNull final PaymentResultViewModel.Builder builder) {
        builder
            .setIsErrorRecoverable(true)
            .setBadgeResId(R.drawable.px_badge_pending_orange)
            .setBackgroundColor(R.color.ui_components_warning_color);
    }

    private void setPendingResources(@NonNull final PaymentResultViewModel.Builder builder,
        @NonNull final String statusDetail) {
        if (StatusHelper.isPendingStatusDetailSuccess(statusDetail)) {
            builder
                .setBackgroundColor(R.color.ui_components_success_color)
                .setBadgeResId(R.drawable.px_badge_check);
        } else {
            builder
                .setBadgeResId(R.drawable.px_badge_pending_orange)
                .setBackgroundColor(R.color.ui_components_warning_color);
        }
    }

    private PaymentResultViewModel.Builder unknownStatusFallback(final PaymentResultViewModel.Builder builder,
        final String status, final String detail) {

        // Generate a friction event with status info
        generateFrictionEvent(status, detail);

        setNonRecoverableErrorResources(builder);
        return builder
            .setTitleResId(R.string.px_title_other_reason_rejection)
            .setMainAction(new ChangePaymentMethodAction())
            .setMainActionTitle(R.string.px_change_payment_method)
            .setLinkAction(null);
    }
}
