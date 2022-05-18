package com.mercadopago.android.px.internal.datasource;

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.CustomSearchItem;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationSolverTest {

    private static final String ACCOUNT_MONEY_SAMPLE_ID = "account_money";
    private static final String CARD_SAMPLE_ID = "1234";

    private static final String HASH_SAMPLE_ACCOUNT_MONEY_CONFIGURATION = "HASH_ACCOUNT_MONEY_CONFIGURATION";
    private static final String HASH_SAMPLE_SAVED_CARD_CONFIGURATION = "HASH_SAVED_CARD_CONFIGURATION";

    private ConfigurationSolverImpl discountConfigurationSolver;

    @Mock private CustomSearchItem accountMoneyCustomSearchItem;
    @Mock private CustomSearchItem cardCustomSearchItem;
    @Mock private PayerPaymentMethodRepository payerPaymentMethodRepository;
    @Mock private PayerPaymentMethodKey accountMoneyKey;
    @Mock private PayerPaymentMethodKey debitCardKey;

    @Before
    public void setUp() {
        final List<CustomSearchItem> customSearchItems = new ArrayList<>();
        customSearchItems.add(accountMoneyCustomSearchItem);
        customSearchItems.add(cardCustomSearchItem);

        discountConfigurationSolver = new ConfigurationSolverImpl(payerPaymentMethodRepository);

        when(accountMoneyCustomSearchItem.getDefaultAmountConfiguration())
            .thenReturn(HASH_SAMPLE_ACCOUNT_MONEY_CONFIGURATION);
        when(accountMoneyCustomSearchItem.getAmountConfiguration(HASH_SAMPLE_ACCOUNT_MONEY_CONFIGURATION))
            .thenReturn(mock(AmountConfiguration.class));


        when(cardCustomSearchItem.getDefaultAmountConfiguration()).thenReturn(HASH_SAMPLE_SAVED_CARD_CONFIGURATION);
        when(cardCustomSearchItem.getAmountConfiguration(HASH_SAMPLE_SAVED_CARD_CONFIGURATION))
            .thenReturn(mock(AmountConfiguration.class));

        when(payerPaymentMethodRepository.get(ACCOUNT_MONEY_SAMPLE_ID)).thenReturn(accountMoneyCustomSearchItem);
        when(payerPaymentMethodRepository.get(CARD_SAMPLE_ID)).thenReturn(cardCustomSearchItem);
        when(payerPaymentMethodRepository.get(accountMoneyKey)).thenReturn(accountMoneyCustomSearchItem);
        when(payerPaymentMethodRepository.get(debitCardKey)).thenReturn(accountMoneyCustomSearchItem);
    }

    @Test
    public void whenHasConfigurationByAccountMoneyIdThenReturnAccountMoneyConfigurationHash() {
        assertEquals(HASH_SAMPLE_ACCOUNT_MONEY_CONFIGURATION,
            discountConfigurationSolver.getConfigurationHashSelectedFor(ACCOUNT_MONEY_SAMPLE_ID));
    }

    @Test
    public void whenHasConfigurationByCardIdIdThenReturnCardConfigurationHash() {
        assertEquals(HASH_SAMPLE_SAVED_CARD_CONFIGURATION,
            discountConfigurationSolver.getConfigurationHashSelectedFor(CARD_SAMPLE_ID));
    }

    @Test
    public void whenHasNotConfigurationByIdThenReturnEmptyConfiguration() {
        assertEquals("", discountConfigurationSolver.getConfigurationHashSelectedFor("5678"));
    }

    @Test
    public void whenHasConfigurationByAccountMoneyIdAndAccountMoneyTypeThenReturnAccountMoneyConfigurationHash() {
        assertEquals(HASH_SAMPLE_ACCOUNT_MONEY_CONFIGURATION,
            discountConfigurationSolver.getConfigurationHashFor(accountMoneyKey));
    }

    @Test
    public void whenHasConfigurationByCardIdAndCardTypeThenReturnAccountMoneyConfigurationHash() {
        assertEquals(HASH_SAMPLE_ACCOUNT_MONEY_CONFIGURATION,
            discountConfigurationSolver.getConfigurationHashFor(debitCardKey));
    }

    @Test
    public void whenHasNotConfigurationByKeyThenReturnEmptyConfiguration() {
        assertEquals("", discountConfigurationSolver.getConfigurationHashFor(mock(PayerPaymentMethodKey.class)));
    }

    @Test
    public void whenHasNotAmountConfigurationWithPayerPaymentMethodKeyThenReturnNull() {
        assertNull(discountConfigurationSolver.getAmountConfigurationFor(mock(PayerPaymentMethodKey.class)));
    }

    @Test
    public void whenHasNotAmountConfigurationWithCustomOptionIdThenReturnNull() {
        assertNull(discountConfigurationSolver.getAmountConfigurationSelectedFor(anyString()));
    }

    @Test
    public void whenHasAmountConfigurationByCustomOptionThenReturnAmountConfiguration() {
        when(payerPaymentMethodRepository.get(CARD_SAMPLE_ID)).thenReturn(cardCustomSearchItem);

        assertNotNull(discountConfigurationSolver.getAmountConfigurationSelectedFor(CARD_SAMPLE_ID));
    }

    @Test
    public void whenHasAmountConfigurationByCardIdAndCardTypeThenReturnAmountConfiguration() {
        final PayerPaymentMethodKey payerPaymentMethodKey = mock(PayerPaymentMethodKey.class);
        when(payerPaymentMethodRepository.get(payerPaymentMethodKey)).thenReturn(cardCustomSearchItem);

        assertNotNull(discountConfigurationSolver.getAmountConfigurationFor(payerPaymentMethodKey));
    }

    @Test
    public void whenHasAmountConfigurationByAccountMoneyIdAndAccountMoneyTypeThenReturnAmountConfiguration() {
        final PayerPaymentMethodKey payerPaymentMethodKey = mock(PayerPaymentMethodKey.class);
        when(payerPaymentMethodRepository.get(payerPaymentMethodKey)).thenReturn(accountMoneyCustomSearchItem);

        assertNotNull(discountConfigurationSolver.getAmountConfigurationFor(payerPaymentMethodKey));
    }
}
