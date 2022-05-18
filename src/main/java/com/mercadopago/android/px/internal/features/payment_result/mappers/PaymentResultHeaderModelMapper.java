package com.mercadopago.android.px.internal.features.payment_result.mappers;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.features.payment_result.model.Badge;
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesModel;
import com.mercadopago.android.px.internal.util.StatusHelper;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;
import com.mercadopago.android.px.internal.viewmodel.GenericLocalized;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.internal.mappers.Mapper;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.PaymentTypes;

public class PaymentResultHeaderModelMapper extends Mapper<PaymentModel, PaymentResultHeader.Model> {

    private static final int DEFAULT_LABEL = 0;
    private static final int DEFAULT_ICON_IMAGE = R.drawable.px_icon_default;
    private static final int ITEM_ICON_IMAGE = R.drawable.px_icon_product;
    private static final int CARD_ICON_IMAGE = R.drawable.px_icon_card;
    private static final int BOLETO_ICON_IMAGE = R.drawable.px_icon_boleto;

    //armar componente Badge que va como hijo
    private static final int DEFAULT_BADGE_IMAGE = 0;
    private static final int CHECK_BADGE_IMAGE = R.drawable.px_badge_check;
    private static final int PENDING_BADGE_GREEN_IMAGE = R.drawable.px_badge_pending;

    @NonNull private final PaymentResultScreenConfiguration configuration;
    @NonNull private final PaymentResultViewModelFactory factory;
    @Nullable private final Instruction instruction;
    @NonNull private final RemediesModel remediesModel;

    /* default */ PaymentResultHeaderModelMapper(@NonNull final PaymentResultScreenConfiguration configuration,
        @NonNull final PaymentResultViewModelFactory factory,
        @Nullable final Instruction instruction, @NonNull final RemediesModel remediesModel) {
        this.configuration = configuration;
        this.factory = factory;
        this.instruction = instruction;
        this.remediesModel = remediesModel;
    }

    @Override
    public PaymentResultHeader.Model map(@NonNull final PaymentModel model) {
        final PaymentResult paymentResult = model.getPaymentResult();
        final PaymentResultViewModel viewModel = factory.createPaymentResultViewModel(paymentResult);

        final boolean hasBodyComponent = viewModel.isApprovedSuccess() || viewModel.hasBodyError();

        return new PaymentResultHeader.Model.Builder()
            .setDynamicHeight(!hasBodyComponent)
            .setBackground(resolveBackground(viewModel))
            .setIconImage(resolveIconRes(viewModel, paymentResult))
            .setIconUrl(resolveIconUrl(remediesModel, paymentResult))
            .setBadgeImage(getBadgeImage(paymentResult, viewModel))
            .setBadgeUrl(resolveBadgeUrl(remediesModel))
            .setTitle(new GenericLocalized(resolveTitle(remediesModel), viewModel.getTitleResId()))
            .setLabel(new GenericLocalized(null, DEFAULT_LABEL))
            .build();
    }

    private String resolveTitle(final RemediesModel remediesModel) {
        String title = getInstructionsTitle();

        if (remediesModel.hasRemedies() && remediesModel.getHeader() != null) {
            title = remediesModel.getHeader().getTitle();
        }

        return title;
    }

    @DrawableRes
    private int resolveIconRes(final PaymentResultViewModel viewModel, final PaymentResult paymentResult) {
        return viewModel.getIconResId() > 0 ? viewModel.getIconResId() : getIconImage(paymentResult);
    }

    @ColorRes
    private int resolveBackground(final PaymentResultViewModel viewModel) {
        int backgroundResId = viewModel.getBackgroundResId();

        if (remediesModel.hasRemedies()) {
            backgroundResId = RemediesModel.DECORATOR.resColor;
        }

        return backgroundResId;
    }

    private String resolveIconUrl(final RemediesModel remediesModel, final PaymentResult paymentResult) {
        final RemediesModel.HeaderModel header = remediesModel.getHeader();
        String iconUrl = getIconUrl(paymentResult);

        if (remediesModel.hasRemedies() && header != null && TextUtil.isNotEmpty(header.getIconUrl())) {
            iconUrl = header.getIconUrl();
        }

        return iconUrl;
    }

    private String resolveBadgeUrl(final RemediesModel remediesModel) {
        String iconUrl = "";

        if (remediesModel.hasRemedies() && remediesModel.getHeader() != null) {
            iconUrl = remediesModel.getHeader().getBadgeUrl();
        }

        return iconUrl;
    }

    @Nullable
    private String getInstructionsTitle() {
        if (instruction != null) {
            return instruction.getTitle();
        }
        return null;
    }

    private int getIconImage(@NonNull final PaymentResult paymentResult) {
        final String paymentStatus = paymentResult.getPaymentStatus();
        final String paymentStatusDetail = paymentResult.getPaymentStatusDetail();

        if (configuration.hasCustomizedImageIcon(paymentStatus, paymentStatusDetail)) {
            return configuration.getPreferenceIcon(paymentStatus, paymentStatusDetail);
        } else if (isItemIconImage(paymentResult)) {
            return ITEM_ICON_IMAGE;
        } else if (isCardIconImage(paymentResult)) {
            return CARD_ICON_IMAGE;
        } else if (isBoletoIconImage(paymentResult)) {
            return BOLETO_ICON_IMAGE;
        } else {
            return DEFAULT_ICON_IMAGE;
        }
    }

    @Nullable
    private String getIconUrl(@NonNull final PaymentResult paymentResult) {
        final String paymentStatus = paymentResult.getPaymentStatus();
        final String paymentStatusDetail = paymentResult.getPaymentStatusDetail();
        return configuration.getPreferenceUrlIcon(paymentStatus, paymentStatusDetail);
    }

    private int getBadgeImage(@NonNull final PaymentResult paymentResult,
        @NonNull final PaymentResultViewModel viewModel) {
        if (remediesModel.hasRemedies()) {
            return RemediesModel.DECORATOR.badge;
        } else if (hasCustomizedBadge(paymentResult)) {
            final String badge = getPreferenceBadge(paymentResult);
            switch (badge) {
            case Badge.CHECK_BADGE_IMAGE:
                return CHECK_BADGE_IMAGE;
            case Badge.PENDING_BADGE_IMAGE:
                return PENDING_BADGE_GREEN_IMAGE;
            default:
                return DEFAULT_BADGE_IMAGE;
            }
        } else {
            return viewModel.getBadgeResId();
        }
    }

    private String getPreferenceBadge(@NonNull final PaymentResult paymentResult) {
        if (isStatusApproved(paymentResult)) {
            return configuration.getApprovedBadge();
        }
        return "";
    }

    private boolean isItemIconImage(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();

        return Payment.StatusCodes.STATUS_APPROVED.equalsIgnoreCase(status) ||
            Payment.StatusCodes.STATUS_PENDING.equalsIgnoreCase(status) &&
                StatusHelper.isPendingStatusDetailSuccess(statusDetail);
    }

    private boolean isCardIconImage(@NonNull final PaymentResult paymentResult) {
        if (isPaymentMethodIconImage(paymentResult)) {
            final String paymentTypeId = paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId();

            return PaymentTypes.PREPAID_CARD.equalsIgnoreCase(paymentTypeId) ||
                PaymentTypes.DEBIT_CARD.equalsIgnoreCase(paymentTypeId) ||
                PaymentTypes.CREDIT_CARD.equalsIgnoreCase(paymentTypeId);
        }
        return false;
    }

    private boolean isPaymentMethodIconImage(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();

        return ((Payment.StatusCodes.STATUS_PENDING).equalsIgnoreCase(status) &&
            !Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT.equalsIgnoreCase(statusDetail) ||
            Payment.StatusCodes.STATUS_IN_PROCESS.equalsIgnoreCase(status) ||
            Payment.StatusCodes.STATUS_REJECTED.equalsIgnoreCase(status));
    }

    private boolean isBoletoIconImage(@NonNull final PaymentResult paymentResult) {
        if (isPaymentMethodIconImage(paymentResult)) {
            final String paymentMethodId = paymentResult.getPaymentData().getPaymentMethod().getId();
            return PaymentMethods.BRASIL.BOLBRADESCO.equalsIgnoreCase(paymentMethodId);
        }
        return false;
    }

    private boolean hasCustomizedBadge(@NonNull final PaymentResult paymentResult) {
        if (isStatusApproved(paymentResult)) {
            return configuration.getApprovedBadge() != null && !configuration.getApprovedBadge().isEmpty();
        }
        return false;
    }

    private boolean isStatusApproved(@NonNull final PaymentResult paymentResult) {
        return paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_APPROVED);
    }
}