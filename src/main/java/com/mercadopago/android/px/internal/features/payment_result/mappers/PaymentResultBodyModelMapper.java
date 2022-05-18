package com.mercadopago.android.px.internal.features.payment_result.mappers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.features.business_result.BusinessPaymentResultTracker;
import com.mercadopago.android.px.internal.features.business_result.PaymentCongratsResponseMapper;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsResponse;
import com.mercadopago.android.px.internal.features.payment_congrats.model.CongratsViewModelMapper;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo;
import com.mercadopago.android.px.internal.features.payment_result.model.DisplayInfoHelper;
import com.mercadopago.android.px.internal.mappers.PaymentResultMethodMapper;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;
import com.mercadopago.android.px.internal.util.PaymentDataHelper;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultMethod;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.internal.mappers.Mapper;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentResultBodyModelMapper extends Mapper<PaymentModel, PaymentResultBody.Model> {

    @NonNull private final PaymentResultScreenConfiguration configuration;
    @NonNull private final MPTracker tracker;
    @NonNull private final DisplayInfoHelper displayInfoHelper;
    @NonNull private final PaymentResultMethodMapper paymentResultMethodMapper;

    public PaymentResultBodyModelMapper(@NonNull final PaymentResultScreenConfiguration configuration,
        @NonNull final MPTracker tracker, @NonNull final DisplayInfoHelper displayInfoHelper,
        @NonNull final PaymentResultMethodMapper paymentResultMethodMapper) {
        this.configuration = configuration;
        this.tracker = tracker;
        this.displayInfoHelper = displayInfoHelper;
        this.paymentResultMethodMapper = paymentResultMethodMapper;
    }

    @Override
    public PaymentResultBody.Model map(@NonNull final PaymentModel model) {
        final PaymentResult paymentResult = model.getPaymentResult();
        final List<PaymentResultMethod.Model> paymentResultMethodModels = new ArrayList<>();
        final PaymentCongratsResponse paymentCongratsResponse = new PaymentCongratsResponseMapper()
            .map(model.getCongratsResponse());
        for (final PaymentData paymentData : paymentResult.getPaymentDataList()) {
            final String imageUrl =
                model.getCongratsResponse().getPaymentMethodsImages().get(paymentData.getPaymentMethod().getId());
            paymentResultMethodModels.add(paymentResultMethodMapper.map(getPaymentInfo(imageUrl, paymentData, model.getCurrency())));
        }

        return new PaymentResultBody.Model.Builder()
            .setPaymentResultMethodModels(paymentResultMethodModels)
            .setCongratsViewModel(new CongratsViewModelMapper(new BusinessPaymentResultTracker(tracker))
                .map(paymentCongratsResponse))
            .setReceiptId(String.valueOf(paymentResult.getPaymentId()))
            .setTopFragment(configuration.getTopFragment())
            .setBottomFragment(configuration.getBottomFragment())
            .build();
    }

    private PaymentInfo getPaymentInfo(@Nullable final String imageUrl, @NonNull final PaymentData paymentData, @NonNull final Currency currency) {
        final PaymentInfo.Builder paymentInfoBuilder = new PaymentInfo.Builder()
                .withLastFourDigits(paymentData.getToken() != null ? paymentData.getToken().getLastFourDigits() : null)
                .withPaymentMethodName(paymentData.getPaymentMethod().getName())
                .withIconUrl(imageUrl)
                .withPaymentMethodType(
                        PaymentInfo.PaymentMethodType.fromName(paymentData.getPaymentMethod().getPaymentTypeId()))
                .withPaidAmount(getPrettyAmount(currency,
                        PaymentDataHelper.getPrettyAmountToPay(paymentData)));

        displayInfoHelper.resolve(paymentData, paymentInfoBuilder);

        if (paymentData.getDiscount() != null) {
            paymentInfoBuilder
                    .withDiscountData(paymentData.getDiscount().getName(),
                            getPrettyAmount(currency, paymentData.getNoDiscountAmount()));
        }

        if (paymentData.getPayerCost() != null) {
            paymentInfoBuilder.withInstallmentsData(paymentData.getPayerCost().getInstallments(),
                    getPrettyAmount(currency, paymentData.getPayerCost().getInstallmentAmount()),
                    getPrettyAmount(currency, paymentData.getPayerCost().getTotalAmount()),
                    paymentData.getPayerCost().getInstallmentRate());
        }

        return paymentInfoBuilder.build();
    }

    private static String getPrettyAmount(@NonNull final Currency currency, @NonNull final BigDecimal amount) {
        return CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(currency, amount);
    }
}
