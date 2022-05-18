package com.mercadopago.android.px.internal.datasource;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.core.internal.CheckoutData;
import com.mercadopago.android.px.core.internal.PaymentWrapper;
import com.mercadopago.android.px.core.v2.PaymentProcessor;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceEventHandler;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceHandlerWrapper;
import com.mercadopago.android.px.internal.core.FileManager;
import com.mercadopago.android.px.internal.features.validation_program.ValidationProgramUseCase;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.CongratsRepository;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.EscPaymentManager;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.PaymentConfigurationUtil;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.io.File;
import java.util.List;
import kotlin.Unit;

public class PaymentService implements PaymentRepository {

    private static final String FILE_PAYMENT = "file_payment";

    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final Context context;
    @NonNull private final FileManager fileManager;

    @NonNull /* default */ final PaymentServiceHandlerWrapper handlerWrapper;
    @NonNull /* default */ final AmountConfigurationRepository amountConfigurationRepository;
    @NonNull /* default */ final UserSelectionRepository userSelectionRepository;

    @Nullable private PaymentWrapper payment;
    @NonNull private final File paymentFile;
    @NonNull private final ValidationProgramUseCase validationProgramUseCase;
    @NonNull private final PaymentDataFactory paymentDataFactory;

    public PaymentService(@NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final DisabledPaymentMethodRepository disabledPaymentMethodRepository,
        @NonNull final Context context,
        @NonNull final EscPaymentManager escPaymentManager,
        @NonNull final AmountConfigurationRepository amountConfigurationRepository,
        @NonNull final CongratsRepository congratsRepository,
        @NonNull final FileManager fileManager,
        @NonNull final ValidationProgramUseCase validationProgramUseCase,
        @NonNull final PaymentResultFactory paymentResultFactory,
        @NonNull final PaymentDataFactory paymentDataFactory) {
        this.amountConfigurationRepository = amountConfigurationRepository;
        this.userSelectionRepository = userSelectionRepository;
        this.paymentSettingRepository = paymentSettingRepository;
        this.context = context;
        this.fileManager = fileManager;
        this.validationProgramUseCase = validationProgramUseCase;
        this.paymentDataFactory = paymentDataFactory;

        paymentFile = fileManager.create(FILE_PAYMENT);

        handlerWrapper =
            new PaymentServiceHandlerWrapper(this, disabledPaymentMethodRepository, escPaymentManager,
                congratsRepository, userSelectionRepository,
                paymentSettingRepository.getAdvancedConfiguration().getPostPaymentConfiguration(),
                paymentResultFactory, paymentDataFactory);
    }

    @Nullable
    @Override
    public PaymentServiceEventHandler getObservableEvents() {
        return handlerWrapper.getObservableEvents();
    }

    @Override
    public void reset() {
        fileManager.removeFile(paymentFile);
    }

    @Override
    public void storePayment(@NonNull final IPaymentDescriptor payment) {
        this.payment = new PaymentWrapper(payment);
        fileManager.writeToFile(paymentFile, this.payment);
    }

    @Nullable
    @Override
    public IPaymentDescriptor getPayment() {
        final PaymentWrapper paymentWrapper = getPaymentWrapper();
        return paymentWrapper != null ? paymentWrapper.get() : null;
    }

    @Nullable
    private PaymentWrapper getPaymentWrapper() {
        if (payment == null) {
            payment = fileManager.readParcelable(paymentFile, PaymentWrapper.CREATOR);
        }
        return payment;
    }

    @NonNull
    @Override
    public PaymentRecovery createRecoveryForInvalidESC() {
        return createPaymentRecovery(Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);
    }

    @NonNull
    @Override
    public PaymentRecovery createPaymentRecovery() {
        return createPaymentRecovery(getPayment().getPaymentStatusDetail());
    }

    @NonNull
    private PaymentRecovery createPaymentRecovery(@NonNull final String statusDetail) {
        final Token token = paymentSettingRepository.getToken();
        final Card card = userSelectionRepository.getCard();
        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();
        return new PaymentRecovery(statusDetail, token, card, paymentMethod);
    }

    /**
     * This method presets all user information ahead before the payment is processed.
     */
    @Override
    public void startExpressPayment() {
        handlerWrapper.createTransactionLiveData();
        pay();
    }

    private void pay() {
        final CheckoutPreference checkoutPreference = paymentSettingRepository.getCheckoutPreference();
        final String securityType = paymentSettingRepository.getSecurityType().getValue();
        if (getPaymentProcessor().shouldShowFragmentOnPayment(checkoutPreference)) {
            handlerWrapper.onVisualPayment();
        } else {
            final List<PaymentData> paymentDataList = paymentDataFactory.create();
            validationProgramUseCase.execute(paymentDataList, validationProgramId -> {
                final CheckoutData checkoutData = new CheckoutData(
                    paymentDataList,
                    checkoutPreference,
                    securityType,
                    validationProgramId
                );
                getPaymentProcessor().startPayment(context, checkoutData, handlerWrapper);
                return Unit.INSTANCE;
            });
        }
    }

    @Override
    public boolean isExplodingAnimationCompatible() {
        return !getPaymentProcessor().shouldShowFragmentOnPayment(paymentSettingRepository.getCheckoutPreference());
    }

    private PaymentProcessor getPaymentProcessor() {
        return PaymentConfigurationUtil.getPaymentProcessor(paymentSettingRepository.getPaymentConfiguration());
    }

    @Override
    public int getPaymentTimeout() {
        return getPaymentProcessor().getPaymentTimeout(paymentSettingRepository.getCheckoutPreference());
    }
}