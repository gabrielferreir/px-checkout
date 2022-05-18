package com.mercadopago.android.px.internal.callbacks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.mercadopago.android.px.configuration.PostPaymentConfiguration;
import com.mercadopago.android.px.internal.datasource.PaymentDataFactory;
import com.mercadopago.android.px.internal.datasource.PaymentResultFactory;
import com.mercadopago.android.px.internal.repository.CongratsRepository;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.EscPaymentManager;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.IPaymentDescriptorHandler;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;
import kotlin.Unit;

public final class PaymentServiceHandlerWrapper implements PaymentServiceHandler {

    //TODO Remove handler when all views use PayButton or LiveData
    @Nullable private WeakReference<PaymentServiceHandler> handler;
    private PaymentServiceEventHandler eventHandler;
    @NonNull private final EscPaymentManager escPaymentManager;
    @NonNull private final CongratsRepository congratsRepository;
    private final UserSelectionRepository userSelectionRepository;
    @NonNull private final Queue<Message> messages;
    @NonNull /* default */ final PaymentRepository paymentRepository;
    @NonNull /* default */ final DisabledPaymentMethodRepository disabledPaymentMethodRepository;
    @NonNull /* default */ final PostPaymentConfiguration postPaymentConfiguration;
    @NonNull /* default */ final PaymentResultFactory paymentResultFactory;
    @NonNull /* default */ final PaymentDataFactory paymentDataFactory;

    @NonNull private final IPaymentDescriptorHandler paymentHandler = new IPaymentDescriptorHandler() {
        @Override
        public void visit(@NonNull final IPaymentDescriptor payment) {
            final boolean shouldRecoverEsc = verifyAndHandleEsc(payment);

            if (shouldRecoverEsc) {
                onRecoverPaymentEscInvalid(paymentRepository.createRecoveryForInvalidESC());
            } else {
                paymentRepository.storePayment(payment);
                if (isPostPaymentFlow(payment)) {
                    onPostPayment(payment);
                } else {
                    //Must be after store
                    final PaymentResult paymentResult = paymentResultFactory.create(payment);
                    disabledPaymentMethodRepository.handleRejectedPayment(paymentResult);
                    onFetchCongratsResponse(payment, paymentResult);
                }
            }
        }

        @Override
        public void visit(@NonNull final BusinessPayment businessPayment) {
            verifyAndHandleEsc(businessPayment);
            paymentRepository.storePayment(businessPayment);
            if (isPostPaymentFlow(businessPayment)) {
                onPostPayment(businessPayment);
            } else {
                final PaymentResult paymentResult = paymentResultFactory.create(businessPayment);
                disabledPaymentMethodRepository.handleRejectedPayment(paymentResult);
                onFetchCongratsResponse(businessPayment, paymentResult);
            }
        }
    };

    boolean isPostPaymentFlow(final IPaymentDescriptor iPaymentDescriptor) {
        return postPaymentConfiguration.hasPostPaymentUrl()
            && Payment.StatusCodes.STATUS_APPROVED.equals(iPaymentDescriptor.getPaymentStatus());
    }

    public PaymentServiceHandlerWrapper(
        @NonNull final PaymentRepository paymentRepository,
        @NonNull final DisabledPaymentMethodRepository disabledPaymentMethodRepository,
        @NonNull final EscPaymentManager escPaymentManager,
        @NonNull final CongratsRepository congratsRepository,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final PostPaymentConfiguration postPaymentConfiguration,
        @NonNull final PaymentResultFactory paymentResultFactory,
        @NonNull final PaymentDataFactory paymentDataFactory
    ) {
        this.paymentRepository = paymentRepository;
        this.disabledPaymentMethodRepository = disabledPaymentMethodRepository;
        this.escPaymentManager = escPaymentManager;
        this.congratsRepository = congratsRepository;
        this.userSelectionRepository = userSelectionRepository;
        this.postPaymentConfiguration = postPaymentConfiguration;
        this.paymentResultFactory = paymentResultFactory;
        this.paymentDataFactory = paymentDataFactory;
        messages = new LinkedList<>();
    }

    @Nullable
    public PaymentServiceEventHandler getObservableEvents() {
        return eventHandler;
    }

    public void createTransactionLiveData() {
        eventHandler = new PaymentServiceEventHandler();
    }

    public void setHandler(@Nullable final PaymentServiceHandler handler) {
        this.handler = new WeakReference<>(handler);
    }

    public void detach(@Nullable final PaymentServiceHandler handler) {
        if (handler != null && this.handler != null && this.handler.get() != null &&
            this.handler.get().hashCode() == handler.hashCode()) {
            this.handler = null;
        }
    }

    @Override
    public void onVisualPayment() {
        addAndProcess(new VisualPaymentMessage());
    }

    @Override
    public void onRecoverPaymentEscInvalid(final PaymentRecovery recovery) {
        addAndProcess(new RecoverPaymentEscInvalidMessage(recovery));
    }

    boolean verifyAndHandleEsc(@NonNull final IPaymentDescriptor genericPayment) {
        boolean shouldRecoverEsc = false;
        final String paymentTypeId = userSelectionRepository.getPaymentMethod().getPaymentTypeId();
        if (paymentTypeId == null || PaymentTypes.isCardPaymentType(paymentTypeId)) {
            shouldRecoverEsc = handleEsc(genericPayment);
        }
        return shouldRecoverEsc;
    }

    @Override
    public void onPaymentFinished(@NonNull final IPaymentDescriptor payment) {
        // TODO remove - v5 when paymentTypeId is mandatory for payments
        payment.process(getHandler());
    }

    void onFetchCongratsResponse(@NonNull final IPaymentDescriptor payment,
        @NonNull final PaymentResult paymentResult) {
        congratsRepository.getPostPaymentData(payment, paymentResult, this::onPostPayment);
    }

    void onPostPayment(@NonNull final IPaymentDescriptor payment) {
        onPostPaymentFlowStarted(payment);
    }

    @Override
    public void onPostPayment(@NonNull final PaymentModel paymentModel) {
        addAndProcess(new PaymentFinishedMessage(paymentModel));
    }

    @Override
    public void onPostPaymentFlowStarted(@NonNull final IPaymentDescriptor iPaymentDescriptor) {
        addAndProcess(new PostPaymentFlowStartedMessage(iPaymentDescriptor));
    }

    /* default */
    @VisibleForTesting
    @NonNull
    IPaymentDescriptorHandler getHandler() {
        return paymentHandler;
    }

    @Override
    public void onPaymentError(@NonNull final MercadoPagoError error) {
        if (handleEsc(error)) {
            // TODO we should not have this error anymore with cap check backend side.
            onRecoverPaymentEscInvalid(paymentRepository.createRecoveryForInvalidESC());
        } else {
            addAndProcess(new ErrorMessage(error));
        }
    }

    private boolean handleEsc(@NonNull final MercadoPagoError error) {
        return escPaymentManager.manageEscForError(error, paymentDataFactory.create());
    }

    private boolean handleEsc(@NonNull final IPayment payment) {
        return escPaymentManager.manageEscForPayment(paymentDataFactory.create(),
            payment.getPaymentStatus(),
            payment.getPaymentStatusDetail());
    }

    /* default */ void addAndProcess(@NonNull final Message message) {
        messages.add(message);
        processMessages();
    }

    public void processMessages() {
        final PaymentServiceHandler currentHandler = handler != null ? handler.get() : null;
        while (!messages.isEmpty()) {
            final Message polledMessage = messages.poll();
            polledMessage.processMessage(currentHandler, eventHandler);
        }
    }

    //region messages

    private interface Message {
        void processMessage(@Nullable final PaymentServiceHandler handler,
            @NonNull final PaymentServiceEventHandler eventHandler);
    }

    private static class RecoverPaymentEscInvalidMessage implements Message {

        private final PaymentRecovery recovery;

        /* default */ RecoverPaymentEscInvalidMessage(final PaymentRecovery recovery) {
            this.recovery = recovery;
        }

        @Override
        public void processMessage(@Nullable final PaymentServiceHandler handler,
            @Nullable final PaymentServiceEventHandler eventHandler) {
            if (handler != null) {
                handler.onRecoverPaymentEscInvalid(recovery);
            }
            if (eventHandler != null) {
                eventHandler.getRecoverInvalidEscLiveData().setValue(recovery);
            }
        }
    }

    private static class PaymentFinishedMessage implements Message {

        @NonNull private final PaymentModel paymentModel;

        /* default */ PaymentFinishedMessage(@NonNull final PaymentModel paymentModel) {
            this.paymentModel = paymentModel;
        }

        @Override
        public void processMessage(@Nullable final PaymentServiceHandler handler,
            @Nullable final PaymentServiceEventHandler eventHandler) {
            if (handler != null) {
                handler.onPostPayment(paymentModel);
            }
            if (eventHandler != null) {
                eventHandler.getPaymentFinishedLiveData().setValue(paymentModel);
            }
        }
    }

    private static class PostPaymentFlowStartedMessage implements Message {

        @NonNull private final IPaymentDescriptor iPaymentDescriptor;

        /* default */ PostPaymentFlowStartedMessage(
            @NonNull final IPaymentDescriptor iPaymentDescriptor
        ) {
            this.iPaymentDescriptor = iPaymentDescriptor;
        }

        @Override
        public void processMessage(@Nullable final PaymentServiceHandler handler,
            @Nullable final PaymentServiceEventHandler eventHandler) {
            if (handler != null) {
                handler.onPostPaymentFlowStarted(iPaymentDescriptor);
            }

            if (eventHandler != null) {
                eventHandler.getPostPaymentStartedLiveData().setValue(iPaymentDescriptor);
            }
        }
    }

    private static class ErrorMessage implements Message {

        @NonNull private final MercadoPagoError error;

        /* default */ ErrorMessage(@NonNull final MercadoPagoError error) {
            this.error = error;
        }

        @Override
        public void processMessage(@Nullable final PaymentServiceHandler handler,
            @Nullable final PaymentServiceEventHandler eventHandler) {
            if (handler != null) {
                handler.onPaymentError(error);
            }
            if (eventHandler != null) {
                eventHandler.getPaymentErrorLiveData().setValue((error));
            }
        }
    }

    private static class VisualPaymentMessage implements Message {
        @Override
        public void processMessage(@Nullable final PaymentServiceHandler handler,
            @Nullable final PaymentServiceEventHandler eventHandler) {
            if (handler != null) {
                handler.onVisualPayment();
            }
            if (eventHandler != null) {
                eventHandler.getVisualPaymentLiveData().setValue(Unit.INSTANCE);
            }
        }
    }

    //endregion
}