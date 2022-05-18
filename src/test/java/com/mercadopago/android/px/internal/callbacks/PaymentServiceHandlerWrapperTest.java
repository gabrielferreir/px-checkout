package com.mercadopago.android.px.internal.callbacks;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.mercadopago.android.px.configuration.PostPaymentConfiguration;
import com.mercadopago.android.px.internal.datasource.PaymentDataFactory;
import com.mercadopago.android.px.internal.datasource.PaymentResultFactory;
import com.mercadopago.android.px.internal.repository.CongratsRepository;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.EscPaymentManager;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.mocks.PaymentMethodStub;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.IPaymentDescriptorHandler;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceHandlerWrapperTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private PaymentServiceHandler wrapped;
    @Mock private PaymentRepository paymentRepository;
    @Mock private DisabledPaymentMethodRepository disabledPaymentMethodRepository;
    @Mock private PaymentRecovery paymentRecovery;
    @Mock private CongratsRepository congratsRepository;
    @Mock private EscPaymentManager escPaymentManager;
    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private PostPaymentConfiguration postPaymentConfiguration;
    @Mock private PaymentResultFactory paymentResultFactory;
    @Mock private PaymentDataFactory paymentDataFactory;

    private PaymentServiceHandlerWrapper paymentServiceHandlerWrapper;

    @Before
    public void setUp() {
        paymentServiceHandlerWrapper =
            new PaymentServiceHandlerWrapper(
                paymentRepository,
                disabledPaymentMethodRepository,
                escPaymentManager,
                congratsRepository,
                userSelectionRepository,
                postPaymentConfiguration,
                paymentResultFactory,
                paymentDataFactory
            );
        paymentServiceHandlerWrapper.setHandler(wrapped);
        when(paymentRepository.createRecoveryForInvalidESC()).thenReturn(paymentRecovery);
        when(paymentDataFactory.create()).thenReturn(Collections.singletonList(mock(PaymentData.class)));
        when(userSelectionRepository.getPaymentMethod()).thenReturn(PaymentMethodStub.VISA_CREDIT.get());
    }

    private void noMoreInteractions() {
        verifyNoMoreInteractions(wrapped);
        verifyNoMoreInteractions(escPaymentManager);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    public void whenVisualRequired() {
        paymentServiceHandlerWrapper.onVisualPayment();
        verify(wrapped).onVisualPayment();
        noMoreInteractions();
    }

    @Test
    public void whenRecoverPaymentEscInvalidVerifyRecoverPaymentEscInvalid() {
        paymentServiceHandlerWrapper.onRecoverPaymentEscInvalid(paymentRecovery);
        verify(wrapped).onRecoverPaymentEscInvalid(paymentRecovery);
        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithPaymentVerifyEscManaged() {
        final PaymentResult paymentResult = mock(PaymentResult.class);
        final Payment payment = mock(Payment.class);
        when(paymentResultFactory.create(payment)).thenReturn(paymentResult);

        final IPaymentDescriptorHandler handler = paymentServiceHandlerWrapper.getHandler();
        handler.visit(payment);

        verify(escPaymentManager)
            .manageEscForPayment(paymentDataFactory.create(), payment.getPaymentStatus(),
                payment.getPaymentStatusDetail());

        verify(paymentResultFactory).create(payment);
        verify(paymentRepository).storePayment(payment);
        verify(paymentDataFactory, times(2)).create();
        verifyOnPaymentFinished(payment, paymentResult);
        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithBusinessVerifyEscManaged() {
        final BusinessPayment payment = mock(BusinessPayment.class);
        final PaymentResult paymentResult = mock(PaymentResult.class);
        when(paymentResultFactory.create(payment)).thenReturn(paymentResult);

        final IPaymentDescriptorHandler handler = paymentServiceHandlerWrapper.getHandler();
        handler.visit(payment);

        verify(escPaymentManager)
            .manageEscForPayment(paymentDataFactory.create(), payment.getPaymentStatus(),
                payment.getPaymentStatusDetail());

        verify(paymentRepository).storePayment(payment);

        verify(paymentResultFactory).create(payment);

        verify(paymentDataFactory, times(2)).create();
        verifyOnPaymentFinished(payment, paymentResult);
        noMoreInteractions();
    }

    private void verifyOnPaymentFinished(@NonNull final IPaymentDescriptor payment,
        @NonNull final PaymentResult paymentResult) {
        final ArgumentCaptor<CongratsRepository.PostPaymentCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(
            CongratsRepository.PostPaymentCallback.class);
        verify(congratsRepository).getPostPaymentData(eq(payment), eq(paymentResult), callbackArgumentCaptor.capture());
        final CongratsRepository.PostPaymentCallback value = callbackArgumentCaptor.getValue();
        final PaymentModel paymentModel = mock(PaymentModel.class);
        value.handleResult(paymentModel);
        verify(wrapped).onPostPayment(paymentModel);
    }

    @Test
    public void whenPaymentFinishedWithGenericPaymentVerifyEscManaged() {
        final PaymentResult paymentResult = mock(PaymentResult.class);
        final IPaymentDescriptor payment = mock(IPaymentDescriptor.class);
        when(paymentResultFactory.create(payment)).thenReturn(paymentResult);

        final IPaymentDescriptorHandler handler = paymentServiceHandlerWrapper.getHandler();
        handler.visit(payment);

        verify(escPaymentManager)
            .manageEscForPayment(paymentDataFactory.create(), payment.getPaymentStatus(),
                payment.getPaymentStatusDetail());

        verify(paymentRepository).storePayment(payment);
        verify(paymentResultFactory).create(payment);
        verify(paymentDataFactory, times(2)).create();
        verifyOnPaymentFinished(payment, paymentResult);
        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithErrorVerifyEscManaged() {
        final MercadoPagoError error = mock(MercadoPagoError.class);
        paymentServiceHandlerWrapper.onPaymentError(error);

        verify(escPaymentManager).manageEscForError(error, paymentDataFactory.create());

        verify(paymentDataFactory, times(2)).create();
        verify(wrapped).onPaymentError(error);
        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithGenericPaymentAndEscIsInvalidatedVerifyRecoveryCalled() {
        final IPaymentDescriptor payment = mock(IPaymentDescriptor.class);

        when(escPaymentManager.manageEscForPayment(paymentDataFactory.create(), payment.getPaymentStatus(),
            payment.getPaymentStatusDetail())).thenReturn(true);

        final IPaymentDescriptorHandler handler = paymentServiceHandlerWrapper.getHandler();
        handler.visit(payment);

        verify(paymentRepository).createRecoveryForInvalidESC();

        verify(escPaymentManager)
            .manageEscForPayment(paymentDataFactory.create(), payment.getPaymentStatus(),
                payment.getPaymentStatusDetail());

        verify(paymentDataFactory, times(3)).create();
        verify(wrapped).onRecoverPaymentEscInvalid(paymentRecovery);

        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithPaymentAndEscIsInvalidatedVerifyRecoveryCalled() {
        final Payment payment = mock(Payment.class);

        when(escPaymentManager.manageEscForPayment(paymentDataFactory.create(), payment.getPaymentStatus(),
            payment.getPaymentStatusDetail())).thenReturn(true);

        final IPaymentDescriptorHandler handler = paymentServiceHandlerWrapper.getHandler();
        handler.visit(payment);

        verify(escPaymentManager)
            .manageEscForPayment(paymentDataFactory.create(), payment.getPaymentStatus(),
                payment.getPaymentStatusDetail());

        verify(paymentRepository).createRecoveryForInvalidESC();
        verify(paymentDataFactory, times(3)).create();
        verify(wrapped).onRecoverPaymentEscInvalid(paymentRecovery);

        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithErrorAndEscIsInvalidatedVerifyRecoveryCalled() {
        final MercadoPagoError error = mock(MercadoPagoError.class);

        when(escPaymentManager.manageEscForError(error, paymentDataFactory.create())).thenReturn(true);

        paymentServiceHandlerWrapper.onPaymentError(error);

        verify(escPaymentManager).manageEscForError(error, paymentDataFactory.create());
        verify(paymentRepository).createRecoveryForInvalidESC();
        verify(paymentDataFactory, times(3)).create();
        verify(wrapped).onRecoverPaymentEscInvalid(paymentRecovery);

        noMoreInteractions();
    }

    @Test
    public void whenPaymentThenVerifyPostPaymentFlowStarted() {
        final Payment payment = mock(Payment.class);

        when(payment.getPaymentStatus()).thenReturn(Payment.StatusCodes.STATUS_APPROVED);
        when(postPaymentConfiguration.hasPostPaymentUrl()).thenReturn(true);

        final IPaymentDescriptorHandler handler = paymentServiceHandlerWrapper.getHandler();
        handler.visit(payment);
        verify(wrapped).onPostPaymentFlowStarted(payment);
    }

    @Test
    public void whenBusinessPaymentThenVerifyPostPaymentFlowStarted() {
        final BusinessPayment businessPayment = mock(BusinessPayment.class);

        when(businessPayment.getPaymentStatus()).thenReturn(Payment.StatusCodes.STATUS_APPROVED);
        when(postPaymentConfiguration.hasPostPaymentUrl()).thenReturn(true);

        final IPaymentDescriptorHandler handler = paymentServiceHandlerWrapper.getHandler();
        handler.visit(businessPayment);
        verify(wrapped).onPostPaymentFlowStarted(businessPayment);
    }
}
