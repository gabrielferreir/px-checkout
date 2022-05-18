package com.mercadopago.android.px.internal.util;

import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.Token;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EscUtilTest {

    private static final String STUB_ESC = "fake esc";
    @Mock private PaymentData paymentData;
    @Mock private Token token;
    private static final List<String> ESC_BLACKLISTED_STATUS =
        Collections.singletonList(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_HIGH_RISK);

    @Test
    public void whenNullPaymentDataShouldDeleteEscFalse() {
        final boolean shouldDeleteEsc = EscUtil.shouldDeleteEsc(ESC_BLACKLISTED_STATUS, null,
            Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);
        assertFalse(shouldDeleteEsc);
    }

    @Test
    public void whenPaymentDoesNotHasCardInfoShouldDeleteEscFalse() {
        when(paymentData.containsCardInfo()).thenReturn(false);
        final boolean shouldDeleteEsc = EscUtil.shouldDeleteEsc(ESC_BLACKLISTED_STATUS, paymentData,
            Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);
        assertFalse(shouldDeleteEsc);
    }

    @Test
    public void whenPaymentHasCardInfoAndStatusNotApprovedShouldDeleteEscFalse() {
        when(paymentData.containsCardInfo()).thenReturn(true);
        final boolean shouldDeleteEsc = EscUtil.shouldDeleteEsc(ESC_BLACKLISTED_STATUS, paymentData,
            Payment.StatusCodes.STATUS_REJECTED,
            null);
        assertFalse(shouldDeleteEsc);
    }

    @Test
    public void whenPaymentHasCardInfoAndStatusRejectedHighRiskShouldDeleteEscTrue() {
        when(paymentData.containsCardInfo()).thenReturn(true);
        final boolean shouldDeleteEsc = EscUtil.shouldDeleteEsc(ESC_BLACKLISTED_STATUS, paymentData,
            Payment.StatusCodes.STATUS_REJECTED,
            Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_HIGH_RISK);
        assertTrue(shouldDeleteEsc);
    }

    @Test
    public void whenPaymentHasCardInfoAndStatusApprovedShouldDeleteEscFalse() {
        when(paymentData.containsCardInfo()).thenReturn(true);
        final boolean shouldDeleteEsc = EscUtil.shouldDeleteEsc(ESC_BLACKLISTED_STATUS, paymentData,
            Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);
        assertFalse(shouldDeleteEsc);
    }

    @Test
    public void whenPaymentHasCardInfoAndEscAndStatusApprovedShouldStoreEscTrue() {
        when(token.getEsc()).thenReturn(STUB_ESC);
        when(paymentData.getToken()).thenReturn(token);
        when(paymentData.containsCardInfo()).thenReturn(true);
        final boolean shouldStoreESC = EscUtil.shouldStoreESC(ESC_BLACKLISTED_STATUS, paymentData,
            Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);
        assertTrue(shouldStoreESC);
    }

    @Test
    public void whenPaymentHasCardInfoAndNotEscAndStatusApprovedShouldStoreEscFalse() {
        when(token.getEsc()).thenReturn(null);
        when(paymentData.getToken()).thenReturn(token);
        when(paymentData.containsCardInfo()).thenReturn(true);
        final boolean shouldStoreESC = EscUtil.shouldStoreESC(ESC_BLACKLISTED_STATUS, paymentData,
            Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);
        assertFalse(shouldStoreESC);
    }
}