package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.actions.ChangePaymentMethodAction;
import com.mercadopago.android.px.internal.actions.NextAction;
import com.mercadopago.android.px.internal.actions.RecoverPaymentAction;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Footer;
import com.mercadopago.android.px.mocks.PaymentResults;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FooterPaymentResultTest {

    private static final String LABEL_CONTINUE = "Continue";
    private static final String LABEL_CHANGE = "Pay using a different method";
    private static final String LABEL_ALREADY_ACTIVATED = "Already activated";
    private static final String LABEL_ALREADY_AUTHORIZED = "Already authorized";
    private static final String LABEL_REVIEW_TC_INFO = "REVIEW INFO";
    private static final String LABEL_OK = "ok";
    private static final String LABEL_GO_TO_HOME = "Go to Home";

    @Mock private Context context;
    @Mock private ActionDispatcher actionDispatcher;
    private final PaymentResultViewModelFactory factory = new PaymentResultViewModelFactory(mock(MPTracker.class));

    @Before
    public void setup() {
        new PaymentResultScreenConfiguration.Builder().build();
    }

    @Test
    public void testApproved() {
        when(context.getString(R.string.px_button_continue)).thenReturn(LABEL_CONTINUE);
        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        final FooterPaymentResult footerPaymentResult = new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNull(props.buttonAction);
        assertNotNull(props.linkAction);

        assertEquals(LABEL_CONTINUE, props.linkAction.label);
        assertNotNull(props.linkAction.action);
        assertTrue(props.linkAction.action instanceof NextAction);
    }

    @Test
    public void testRejectedCardDisabledPaymentResult() {
        when(context.getString(R.string.px_text_card_enabled)).thenReturn(LABEL_ALREADY_ACTIVATED);
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedCardDisabled();
        final FooterPaymentResult footerPaymentResult = new FooterPaymentResult(factory, paymentResult, actionDispatcher);
        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNotNull(props.buttonAction);
        assertEquals(LABEL_ALREADY_ACTIVATED, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof RecoverPaymentAction);
        assertNotNull(props.linkAction);
        assertTrue(LABEL_CHANGE, props.linkAction.action instanceof ChangePaymentMethodAction);
    }

    @Test
    public void testRejectedOtherReasonPaymentResult() {
        when(context.getString(R.string.px_change_payment_method)).thenReturn(LABEL_CHANGE);
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final FooterPaymentResult footerPaymentResult = new FooterPaymentResult(factory, paymentResult, actionDispatcher);
        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CHANGE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertNull(props.linkAction);
        assertTrue(props.buttonAction.action instanceof ChangePaymentMethodAction);
    }

    @Test
    public void testRejectedBadFilledDatePaymentResult() {

        when(context.getString(R.string.px_text_pay_with_other_method)).thenReturn(LABEL_CHANGE);
        when(context.getString(R.string.px_error_bad_filled_action)).thenReturn(LABEL_REVIEW_TC_INFO);

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledDatePaymentResult();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNotNull(props.buttonAction);
        assertEquals(LABEL_REVIEW_TC_INFO, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof RecoverPaymentAction);

        assertNotNull(props.linkAction);
        assertEquals(LABEL_CHANGE, props.linkAction.label);
        assertNotNull(props.linkAction.action);
        assertTrue(props.linkAction.action instanceof ChangePaymentMethodAction);
    }

    @Test
    public void testRejectedInsufficientAmountPaymentResult() {

        when(context.getString(R.string.px_change_payment_method)).thenReturn(LABEL_CHANGE);
        when(context.getString(R.string.px_button_text_go_to_home)).thenReturn(LABEL_GO_TO_HOME);

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CHANGE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof ChangePaymentMethodAction);

        assertNotNull(props.linkAction);
        assertEquals(LABEL_GO_TO_HOME, props.linkAction.label);
        assertNotNull(props.linkAction.action);
        assertTrue(props.linkAction.action instanceof NextAction);
    }

    @Test
    public void testRejectedMaxAttemptsPaymentResult() {

        when(context.getString(R.string.px_change_payment_method)).thenReturn(LABEL_CHANGE);
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedMaxAttemptsPaymentResult();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CHANGE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof ChangePaymentMethodAction);

        assertNull(props.linkAction);
    }

    @Test
    public void testRejectedCallForAuth() {
        when(context.getString(R.string.px_text_authorized_call_for_authorize)).thenReturn(LABEL_ALREADY_AUTHORIZED);
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final FooterPaymentResult footerPaymentResult = new FooterPaymentResult(factory, paymentResult, actionDispatcher);
        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNotNull(props.buttonAction);
        assertEquals(LABEL_ALREADY_AUTHORIZED, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof RecoverPaymentAction);
        assertNotNull(props.linkAction);
        assertTrue(LABEL_CHANGE, props.linkAction.action instanceof ChangePaymentMethodAction);
    }

    @Test
    public void testRejectedCreditCardInsufficientAmountPaymentResult() {
        when(context.getString(R.string.px_change_payment_method)).thenReturn(LABEL_CHANGE);

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedCreditCardInsufficientAmountPaymentResult();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNull(props.linkAction);

        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CHANGE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof ChangePaymentMethodAction);
    }

    @Test
    public void testContingencyAndUnsupported() {
        when(context.getString(R.string.px_got_it)).thenReturn(LABEL_OK);

        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNull(props.buttonAction);

        assertNotNull(props.linkAction);
        assertEquals(LABEL_OK, props.linkAction.label);
        assertNotNull(props.linkAction.action);
        assertTrue(props.linkAction.action instanceof NextAction);
    }

    @Test
    public void testReviewManual() {
        when(context.getString(R.string.px_got_it)).thenReturn(LABEL_OK);

        final PaymentResult paymentResult = PaymentResults.getStatusInProcessReviewManualPaymentResult();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNull(props.buttonAction);

        assertNotNull(props.linkAction);
        assertEquals(LABEL_OK, props.linkAction.label);
        assertNotNull(props.linkAction.action);
        assertTrue(props.linkAction.action instanceof NextAction);
    }

    @Test
    public void testRejected() {
        when(context.getString(R.string.px_change_payment_method)).thenReturn(LABEL_CHANGE);
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final FooterPaymentResult footerPaymentResult = new FooterPaymentResult(factory, paymentResult, actionDispatcher);
        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CHANGE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof ChangePaymentMethodAction);
        assertNull(props.linkAction);
    }

    @Test
    public void testRejectedDuplicatedPaymentResult() {
        when(context.getString(R.string.px_got_it)).thenReturn(LABEL_OK);

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedDuplicatedPaymentResult();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNull(props.buttonAction);

        assertNotNull(props.linkAction);
        assertEquals(LABEL_OK, props.linkAction.label);
        assertNotNull(props.linkAction.action);
        assertTrue(props.linkAction.action instanceof NextAction);
    }


    @Test
    public void testRejectedBlackListPaymentResult() {
        when(context.getString(R.string.px_change_payment_method)).thenReturn(LABEL_CHANGE);

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBlacklist();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNull(props.linkAction);

        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CHANGE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof ChangePaymentMethodAction);
    }

    @Test
    public void testRejectedFraudPaymentResult() {
        when (context.getString(R.string.px_button_continue)).thenReturn(LABEL_CONTINUE);

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedFraud();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNull(props.linkAction);

        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CONTINUE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof NextAction);
    }


    @Test
    public void testRejectedHighRiskPaymentResult() {
        when (context.getString(R.string.px_change_payment_method)).thenReturn(LABEL_CHANGE);

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedHighRisk();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNotNull(props.linkAction);

        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CHANGE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof ChangePaymentMethodAction);
    }

    @Test
    public void testRejectedCapExceededPaymentResult() {
        when(context.getString(R.string.px_change_payment_method)).thenReturn(LABEL_CHANGE);
        when(context.getString(R.string.px_button_text_go_to_home)).thenReturn(LABEL_GO_TO_HOME);

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedCapExceeded();
        final FooterPaymentResult footerPaymentResult =
                new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CHANGE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof ChangePaymentMethodAction);

        assertNotNull(props.linkAction);
        assertEquals(LABEL_GO_TO_HOME, props.linkAction.label);
        assertNotNull(props.linkAction.action);
        assertTrue(props.linkAction.action instanceof NextAction);
    }

    @Test
    public void testRejectedByRegulationsPaymentResult() {
        when (context.getString(R.string.px_change_payment_method)).thenReturn(LABEL_CHANGE);

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedByRegulations();
        final FooterPaymentResult footerPaymentResult =
            new FooterPaymentResult(factory, paymentResult, actionDispatcher);

        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNull(props.linkAction);

        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CHANGE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof ChangePaymentMethodAction);
    }

    @Test
    public void testRejectedUnknown() {
        when(context.getString(R.string.px_change_payment_method)).thenReturn(LABEL_CHANGE);
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedUnknown();
        final FooterPaymentResult footerPaymentResult = new FooterPaymentResult(factory, paymentResult, actionDispatcher);
        final Footer.Props props = footerPaymentResult.getFooterProps(context);

        assertNotNull(props);
        assertNotNull(props.buttonAction);
        assertEquals(LABEL_CHANGE, props.buttonAction.label);
        assertNotNull(props.buttonAction.action);
        assertTrue(props.buttonAction.action instanceof ChangePaymentMethodAction);
        assertNull(props.linkAction);
    }
}
