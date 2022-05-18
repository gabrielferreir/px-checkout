package com.mercadopago.android.px.internal.datasource;

import com.mercadopago.android.px.internal.core.FileManager;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Token;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DisabledPaymentMethodServiceTest {

    private static final String CARD_ID = "123456789";

    private DisabledPaymentMethodRepositoryImpl disabledPaymentMethodRepositoryImpl;

    @Mock private FileManager fileManager;

    @Before
    public void setUp() {
        disabledPaymentMethodRepositoryImpl = new DisabledPaymentMethodRepositoryImpl(fileManager);
    }

    @Test
    public void whenPaymentRejectedWithDisableableStatusVerifyDisableablePaymentManaged() {
        final PaymentResult paymentResult = mock(PaymentResult.class);
        mockPayment(paymentResult, PaymentTypes.ACCOUNT_MONEY, PaymentMethods.ACCOUNT_MONEY);

        disabledPaymentMethodRepositoryImpl.handleRejectedPayment(paymentResult);
        verifyRejectedPaymentManaged(true);
    }

    @Test
    public void whenGenericPaymentRejectedWithDisableableStatusVerifyDisableablePaymentManaged() {
        final PaymentResult paymentResult = mock(PaymentResult.class);
        mockPayment(paymentResult, PaymentTypes.ACCOUNT_MONEY, PaymentMethods.ACCOUNT_MONEY);

        disabledPaymentMethodRepositoryImpl.handleRejectedPayment(paymentResult);

        verifyRejectedPaymentManaged(true);
    }

    @Test
    public void whenBusinessPaymentRejectedWithDisableableStatusVerifyDisableablePaymentManaged() {
        final PaymentResult paymentResult = mock(PaymentResult.class);
        mockPayment(paymentResult, PaymentTypes.ACCOUNT_MONEY, PaymentMethods.ACCOUNT_MONEY);

        disabledPaymentMethodRepositoryImpl.handleRejectedPayment(paymentResult);

        verifyRejectedPaymentManaged(true);
    }

    @Test
    public void whenPaymentRejectedWithDisableableStatusAndCardVerifyDisableablePaymentManaged() {
        final PaymentResult paymentResult = mock(PaymentResult.class);
        mockPayment(paymentResult, PaymentTypes.CREDIT_CARD, PaymentMethods.ARGENTINA.VISA);

        when(paymentResult.getPaymentData().getToken()).thenReturn(mock(Token.class));
        when(paymentResult.getPaymentData().getToken().getCardId()).thenReturn(CARD_ID);

        disabledPaymentMethodRepositoryImpl.handleRejectedPayment(paymentResult);

        verifyRejectedPaymentManaged(true);
    }

    @Test
    public void whenPaymentRejectedWithDisableableStatusAndGuessingCardVerifyDisableablePaymentManaged() {
        final PaymentResult paymentResult = mock(PaymentResult.class);
        mockPayment(paymentResult, PaymentTypes.CREDIT_CARD, PaymentMethods.ARGENTINA.VISA);

        when(paymentResult.getPaymentData().getToken()).thenReturn(mock(Token.class));

        disabledPaymentMethodRepositoryImpl.handleRejectedPayment(paymentResult);

        verifyRejectedPaymentManaged(false);
    }

    private void mockPayment(final PaymentResult paymentResult, final String paymentMethodType,
        final String paymentMethodId) {
        when(paymentResult.getPaymentStatus()).thenReturn(Payment.StatusCodes.STATUS_REJECTED);
        when(paymentResult.getPaymentStatusDetail())
            .thenReturn(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_HIGH_RISK);
        when(paymentResult.getPaymentData()).thenReturn(mock(PaymentData.class));
        when(paymentResult.getPaymentData().getPaymentMethod()).thenReturn(mock(PaymentMethod.class));
        when(paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId()).thenReturn(paymentMethodType);
        when(paymentResult.getPaymentData().getPaymentMethod().getId()).thenReturn(paymentMethodId);
    }

    private void verifyRejectedPaymentManaged(final boolean shouldStorePaymentId) {
        if (shouldStorePaymentId) {
            verify(fileManager).writeToFile(any(), any());
        } else {
            verify(fileManager, times(0)).writeToFile(any(), any());
        }
    }
}