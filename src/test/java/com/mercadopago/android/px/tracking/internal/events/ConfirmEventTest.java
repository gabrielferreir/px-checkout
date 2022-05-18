package com.mercadopago.android.px.tracking.internal.events;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository;
import com.mercadopago.android.px.model.AccountMoneyMetadata;
import com.mercadopago.android.px.model.BankTransferInfo;
import com.mercadopago.android.px.model.CardDisplayInfo;
import com.mercadopago.android.px.model.CardMetadata;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.internal.Application;
import com.mercadopago.android.px.model.internal.Application.PaymentMethod;
import com.mercadopago.android.px.model.internal.BankTransfer;
import com.mercadopago.android.px.model.internal.OneTapItem;
import com.mercadopago.android.px.tracking.internal.BankInfoHelper;
import com.mercadopago.android.px.tracking.internal.mapper.FromApplicationToApplicationInfo;
import com.mercadopago.android.px.tracking.internal.mapper.FromSelectedExpressMetadataToAvailableMethods;
import com.mercadopago.android.px.tracking.internal.model.ConfirmData;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfirmEventTest {

    private static final String EXPECTED_PATH = "/px_checkout/review/confirm";
    private static final String EXPECTED_JUST_CARD =
        "{review_type=one_tap, payment_method_selected_index=2, payment_method_id=visa, payment_method_type=credit_card, extra_info={has_interest_free=false, issuer_id=0, has_split=false, has_reimbursement=false, card_id=123, selected_installment={quantity=1, installment_amount=10, visible_total_price=10, interest_rate=10}, methods_applications=[], has_esc=false}}";
    private static final String EXPECTED_JUST_AM =
        "{review_type=one_tap, payment_method_selected_index=2, payment_method_id=account_money, payment_method_type=account_money, extra_info={has_interest_free=false, balance=10, has_reimbursement=false, invested=true, methods_applications=[]}}";
    private static final int PAYMENT_METHOD_SELECTED_INDEX = 2;
    private static final String EXPECTED_JUST_DEBIN_TRANSFER =
            "{review_type=one_tap, payment_method_selected_index=2, payment_method_id=debin_transfer, payment_method_type=bank_transfer, extra_info={has_interest_free=false, external_account_id=123456, bank_name=banco galicia, has_reimbursement=false, methods_applications=[]}}";

    @Mock private OneTapItem oneTapItem;
    @Mock private Set<String> cardIdsWithEsc;
    @Mock private Application application;
    @Mock private ApplicationSelectionRepository applicationSelectionRepository;
    @Mock private PayerPaymentMethodRepository payerPaymentMethodRepository;
    private BankInfoHelper bankInfoHelper;

    @Before
    public void setUp() {
        final String accountId = "123456";
        final String bankName = "banco galicia";
        final BankTransferInfo bankTransferInfo = mock(BankTransferInfo.class);
        final CustomSearchItem payerPaymentMethod = mock(CustomSearchItem.class);

        when(bankTransferInfo.getName()).thenReturn(bankName);
        when(payerPaymentMethod.getBankInfo()).thenReturn(bankTransferInfo);
        when(payerPaymentMethodRepository.get(accountId)).thenReturn(payerPaymentMethod);

        bankInfoHelper = new BankInfoHelper(payerPaymentMethodRepository);
    }

    @NonNull
    private ConfirmEvent getConfirmEvent(final PayerCost payerCost) {
        final ConfirmData
            confirmTrackerData = new ConfirmData(ConfirmData.ReviewType.ONE_TAP, PAYMENT_METHOD_SELECTED_INDEX,
            new FromSelectedExpressMetadataToAvailableMethods(
                applicationSelectionRepository,
                mock(FromApplicationToApplicationInfo.class),
                cardIdsWithEsc,
                payerCost,
                false,
                bankInfoHelper)
                .map(oneTapItem));
        return new ConfirmEvent(confirmTrackerData);
    }

    @Test
    public void whenExpressMetadataHasAccountMoneyThenShowItInMetadata() {
        final AccountMoneyMetadata am = mock(AccountMoneyMetadata.class);
        final String paymentMethodId = "account_money";
        when(application.getPaymentMethod()).thenReturn(new PaymentMethod(paymentMethodId, paymentMethodId));
        when(applicationSelectionRepository.get(oneTapItem)).thenReturn(application);
        when(oneTapItem.getAccountMoney()).thenReturn(am);
        when(oneTapItem.isAccountMoney()).thenReturn(true);
        when(am.getBalance()).thenReturn(BigDecimal.TEN);
        when(am.isInvested()).thenReturn(true);
        final ConfirmEvent event = getConfirmEvent(mock(PayerCost.class));
        assertEquals(EXPECTED_PATH, event.getTrack().getPath());
        assertEquals(EXPECTED_JUST_AM, event.getTrack().getData().toString());
    }

    @Test
    public void whenExpressMetadataHasSavedCardThenShowItInMetadata() {
        final CardMetadata card = mock(CardMetadata.class);
        final PayerCost payerCost = mock(PayerCost.class);
        final CardDisplayInfo cardDisplayInfo = mock(CardDisplayInfo.class);

        when(payerCost.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(payerCost.getInstallmentAmount()).thenReturn(BigDecimal.TEN);
        when(payerCost.getInstallments()).thenReturn(1);
        when(payerCost.getInstallmentRate()).thenReturn(BigDecimal.TEN);
        when(card.getId()).thenReturn("123");
        when(card.getDisplayInfo()).thenReturn(cardDisplayInfo);

        final String paymentMethodId = "visa";
        final String paymentTypeId = "credit_card";
        when(application.getPaymentMethod()).thenReturn(new PaymentMethod(paymentMethodId, paymentTypeId));
        when(applicationSelectionRepository.get(oneTapItem)).thenReturn(application);
        when(oneTapItem.getCard()).thenReturn(card);

        final ConfirmEvent event = getConfirmEvent(payerCost);
        assertEquals(EXPECTED_PATH, event.getTrack().getPath());
        assertEquals(EXPECTED_JUST_CARD, event.getTrack().getData().toString());
    }

    @Test
    public void whenExpressMetadataHasDebinTransferThenShowItInMetadata() {
        final String paymentMethodId = "debin_transfer";
        final String paymentTypeId = "bank_transfer";
        final String accountId = "123456";
        final BankTransfer bankTransfer = mock(BankTransfer.class);

        when(application.getPaymentMethod()).thenReturn(new PaymentMethod(paymentMethodId, paymentTypeId));
        when(applicationSelectionRepository.get(oneTapItem)).thenReturn(application);
        when(bankTransfer.getId()).thenReturn(accountId);
        when(oneTapItem.getBankTransfer()).thenReturn(bankTransfer);
        when(oneTapItem.getPaymentMethodId()).thenReturn(paymentMethodId);

        final ConfirmEvent event = getConfirmEvent(mock(PayerCost.class));

        assertEquals(EXPECTED_PATH, event.getTrack().getPath());
        assertEquals(EXPECTED_JUST_DEBIN_TRANSFER, event.getTrack().getData().toString());
    }
}