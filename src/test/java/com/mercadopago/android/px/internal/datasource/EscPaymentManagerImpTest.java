package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.EscDeleteReason;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.Configuration;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.mercadopago.android.px.internal.util.ApiUtil.StatusCodes.BAD_REQUEST;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EscPaymentManagerImpTest {

    @Mock private ESCManagerBehaviour escManagerBehaviour;
    @Mock private PaymentSettingRepository paymentSettingRepository;

    private EscPaymentManagerImp escPaymentManager;

    @Before
    public void setUp() {
        escPaymentManager = new EscPaymentManagerImp(escManagerBehaviour, paymentSettingRepository);
        final Configuration configuration = mock(Configuration.class);
        when(paymentSettingRepository.getConfiguration()).thenReturn(configuration);
        when(configuration.getEscBlacklistedStatus())
            .thenReturn(Arrays.asList(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_HIGH_RISK,
                Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE));
    }

    @NonNull
    private PaymentData validCardPaymentData() {
        final Token token = mock(Token.class);
        when(token.getEsc()).thenReturn("fake esc");
        when(token.getCardId()).thenReturn("fake card id");
        final PaymentData paymentData = mock(PaymentData.class);
        when(paymentData.containsCardInfo()).thenReturn(true);
        when(paymentData.getToken()).thenReturn(token);
        return paymentData;
    }

    @NonNull
    private MercadoPagoError escMpError() {
        final MercadoPagoError error = mock(MercadoPagoError.class);
        final ApiException apiException = mock(ApiException.class);
        final Cause cause = mock(Cause.class);
        when(cause.getCode()).thenReturn(ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC);
        when(error.isApiException()).thenReturn(true);
        when(error.getApiException()).thenReturn(apiException);
        when(apiException.getStatus()).thenReturn(BAD_REQUEST);
        when(apiException.getCause()).thenReturn(Collections.singletonList(cause));
        return error;
    }

    @NonNull
    private MercadoPagoError noEscMpError() {
        final MercadoPagoError error = mock(MercadoPagoError.class);
        final ApiException apiException = mock(ApiException.class);
        final Cause cause = mock(Cause.class);
        when(cause.getCode()).thenReturn(ApiException.ErrorCodes.INVALID_CARD_EXPIRATION_MONTH);
        when(error.isApiException()).thenReturn(true);
        when(error.getApiException()).thenReturn(apiException);
        when(apiException.getStatus()).thenReturn(BAD_REQUEST);
        when(apiException.getCause()).thenReturn(Collections.singletonList(cause));
        return error;
    }

    @NonNull
    private MercadoPagoError multipleErrorEscMpError() {
        final MercadoPagoError error = mock(MercadoPagoError.class);
        final ApiException apiException = mock(ApiException.class);
        final Cause cause = mock(Cause.class);
        when(cause.getCode()).thenReturn(ApiException.ErrorCodes.INVALID_CARD_EXPIRATION_MONTH);
        final Cause cause2 = mock(Cause.class);
        when(cause2.getCode()).thenReturn(ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC);
        when(error.isApiException()).thenReturn(true);
        when(error.getApiException()).thenReturn(apiException);
        when(apiException.getStatus()).thenReturn(BAD_REQUEST);
        when(apiException.getCause()).thenReturn(Arrays.asList(cause, cause2));
        return error;
    }

    @Test
    public void whenManageEscForPaymentHasValidPaymentDataAndIsApproveSavesCardReturnFalse() {
        final PaymentData paymentData = validCardPaymentData();
        final boolean invalid =
            escPaymentManager
                .manageEscForPayment(Collections.singletonList(paymentData), Payment.StatusCodes.STATUS_APPROVED,
                    Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);
        verify(escManagerBehaviour).saveESCWith(paymentData.getToken().getCardId(), paymentData.getToken().getEsc());
        verifyNoMoreInteractions(escManagerBehaviour);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForPaymentHasValidPaymentDataAndIsRejectedInvalidEscSaveESCReturnTrue() {
        final PaymentData paymentData = validCardPaymentData();
        final boolean invalid =
            escPaymentManager
                .manageEscForPayment(Collections.singletonList(paymentData), Payment.StatusCodes.STATUS_REJECTED,
                    Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);
        verify(escManagerBehaviour).saveESCWith(eq(paymentData.getToken().getCardId()), anyString());
        verifyNoMoreInteractions(escManagerBehaviour);
        assertTrue(invalid);
    }

    @Test
    public void whenManageEscForPaymentHasValidPaymentDataAndIsRejectedSaveEscReturnFalse() {
        final PaymentData paymentData = validCardPaymentData();
        final boolean invalid =
            escPaymentManager
                .manageEscForPayment(Collections.singletonList(paymentData), Payment.StatusCodes.STATUS_REJECTED,
                    Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);
        verify(escManagerBehaviour).saveESCWith(eq(paymentData.getToken().getCardId()), anyString());
        verifyNoMoreInteractions(escManagerBehaviour);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForPaymentHasValidPaymentDataAndIsRejectedBadFilledSecurityCodeDeleteESCReturnFalse() {
        final PaymentData paymentData = validCardPaymentData();
        final boolean invalid =
            escPaymentManager
                .manageEscForPayment(Collections.singletonList(paymentData), Payment.StatusCodes.STATUS_REJECTED,
                    Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE);
        verify(escManagerBehaviour).deleteESCWith(paymentData.getToken().getCardId(), EscDeleteReason.REJECTED_PAYMENT,
            Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE);
        verifyNoMoreInteractions(escManagerBehaviour);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForPaymentHasNonCardPaymentDataAndIsRejectedDoNothingReturnFalse() {
        final PaymentData paymentData = mock(PaymentData.class);
        final boolean invalid =
            escPaymentManager
                .manageEscForPayment(Collections.singletonList(paymentData), Payment.StatusCodes.STATUS_REJECTED,
                    Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);
        verifyNoMoreInteractions(escManagerBehaviour);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForPaymentHasNonCardPaymentDataAndIsApprovedDoNothingReturnFalse() {
        final PaymentData paymentData = mock(PaymentData.class);
        final boolean invalid =
            escPaymentManager
                .manageEscForPayment(Collections.singletonList(paymentData), Payment.StatusCodes.STATUS_APPROVED,
                    Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);
        verifyNoMoreInteractions(escManagerBehaviour);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForErrorHasCardPaymentDataBadRequestWithCauseInvalidEscDeleteEscAndReturnTrue() {
        final PaymentData paymentData = validCardPaymentData();
        final MercadoPagoError error = escMpError();
        final boolean invalid = escPaymentManager.manageEscForError(error, Collections.singletonList(paymentData));
        verify(escManagerBehaviour).deleteESCWith(paymentData.getToken().getCardId(), EscDeleteReason.REJECTED_PAYMENT,
            ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC);
        verifyNoMoreInteractions(escManagerBehaviour);
        assertTrue(invalid);
    }

    @Test
    public void whenManageEscForErrorHasCardPaymentDataBadRequestWithMultipleCauseAndInvalidEscDeleteEscAndReturnTrue() {
        final PaymentData paymentData = validCardPaymentData();
        final MercadoPagoError error = multipleErrorEscMpError();
        final boolean invalid = escPaymentManager.manageEscForError(error, Collections.singletonList(paymentData));
        verify(escManagerBehaviour).deleteESCWith(paymentData.getToken().getCardId(), EscDeleteReason.REJECTED_PAYMENT,
            ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC);
        verifyNoMoreInteractions(escManagerBehaviour);
        assertTrue(invalid);
    }

    @Test
    public void whenManageEscForErrorHasCardPaymentDataBadRequestWithNoEscCauseReturnFalse() {
        final PaymentData paymentData = validCardPaymentData();
        final MercadoPagoError error = noEscMpError();
        final boolean invalid = escPaymentManager.manageEscForError(error, Collections.singletonList(paymentData));
        verifyNoMoreInteractions(escManagerBehaviour);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForErrorNoCardPaymentDataBadRequestWithNoEscCauseReturnFalse() {
        final PaymentData paymentData = mock(PaymentData.class);
        final MercadoPagoError error = noEscMpError();
        final boolean invalid = escPaymentManager.manageEscForError(error, Collections.singletonList(paymentData));
        verifyNoMoreInteractions(escManagerBehaviour);
        assertFalse(invalid);
    }
}