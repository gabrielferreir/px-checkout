package com.mercadopago.android.px.internal.datasource;

import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.ChargeRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AmountServiceTest {

    @Mock private ChargeRepository chargeRepository;
    @Mock private PaymentSettingRepository paymentSettingRepository;
    @Mock private CheckoutPreference checkoutPreference;
    @Mock private DiscountRepository discountRepository;
    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private AmountConfigurationRepository amountConfigurationRepository;
    @Mock private PayerCost payerCost;
    @Mock private DiscountConfigurationModel discountModel;
    @Mock private Discount discount;
    @Mock private AmountConfiguration amountConfiguration;

    private AmountService amountService;

    private static final DiscountConfigurationModel WITHOUT_DISCOUNT =
        new DiscountConfigurationModel(null, null, false);

    @Before
    public void setUp() {
        amountService = new AmountService(paymentSettingRepository, chargeRepository, discountRepository,
            amountConfigurationRepository);
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(checkoutPreference.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(discountRepository.getCurrentConfiguration()).thenReturn(discountModel);
        when(discountModel.getDiscount()).thenReturn(discount);
        when(discountModel.getDiscount().getCouponAmount()).thenReturn(BigDecimal.ONE);
    }

    @Test
    public void whenHasDiscountAndNoChargesAmountThenGetAmountToPayIsAmountLessDiscount() {
        when(chargeRepository.getChargeAmount(PaymentTypes.CREDIT_CARD)).thenReturn(BigDecimal.ZERO);

        assertEquals(BigDecimal.TEN.subtract(BigDecimal.ONE),
            amountService.getAmountToPay(PaymentTypes.CREDIT_CARD, (PayerCost) null));
    }

    @Test
    public void whenHasNoDiscountAndNoChargesAmountThenGetAmountToPayIsJustAmount() {
        when(chargeRepository.getChargeAmount(PaymentTypes.CREDIT_CARD)).thenReturn(BigDecimal.ZERO);
        when(discountRepository.getCurrentConfiguration()).thenReturn(WITHOUT_DISCOUNT);

        assertEquals(BigDecimal.TEN, amountService.getAmountToPay(PaymentTypes.CREDIT_CARD, (PayerCost) null));
    }

    @Test
    public void whenHasNoDiscountAndHasChargesAmountThenGetAmountToPayIsAmountPlusChargesAmount() {
        when(chargeRepository.getChargeAmount(PaymentTypes.CREDIT_CARD)).thenReturn(BigDecimal.ONE);
        when(discountRepository.getCurrentConfiguration()).thenReturn(WITHOUT_DISCOUNT);

        assertEquals(BigDecimal.TEN.add(BigDecimal.ONE),
            amountService.getAmountToPay(PaymentTypes.CREDIT_CARD, (PayerCost) null));
    }

    @Test
    public void whenHasDiscountAndHasChargesAmountThenGetAmountToPayIsAmountLessDiscountAndPlusChargesAmount() {
        when(chargeRepository.getChargeAmount(PaymentTypes.CREDIT_CARD)).thenReturn(BigDecimal.ONE);

        assertEquals(BigDecimal.TEN, amountService.getAmountToPay(PaymentTypes.CREDIT_CARD, (PayerCost) null));
    }

    @Test
    public void whenGetItemsAmountThenReturnTotalAmount() {
        assertEquals(BigDecimal.TEN, amountService.getItemsAmount());
    }

    @Test
    public void whenHasChargesAmountThenGetItemsAmountPlusCharges() {
        when(chargeRepository.getChargeAmount(PaymentTypes.CREDIT_CARD)).thenReturn(BigDecimal.ONE);

        assertEquals(BigDecimal.TEN.add(BigDecimal.ONE),
            amountService.getItemsPlusCharges(PaymentTypes.CREDIT_CARD));
    }

    @Test
    public void whenHasNoChargesAmountThenGetItemsAmount() {
        when(chargeRepository.getChargeAmount(PaymentTypes.CREDIT_CARD)).thenReturn(BigDecimal.ZERO);

        assertEquals(BigDecimal.TEN, amountService.getItemsPlusCharges(PaymentTypes.CREDIT_CARD));
    }

    @Test
    public void whenGetAppliedChargesAndNoCardChargesReturnOnlyChargesByPaymentMethod() {
        when(chargeRepository.getChargeAmount(PaymentTypes.CREDIT_CARD)).thenReturn(BigDecimal.TEN);

        assertEquals(BigDecimal.TEN, amountService.getAppliedCharges(PaymentTypes.CREDIT_CARD, null));
    }

    @Test
    public void whenGetAppliedChargesAndCardChargesReturnSumOfThem() {
        when(checkoutPreference.getTotalAmount()).thenReturn(BigDecimal.ONE);
        when(discountRepository.getCurrentConfiguration().getDiscount()).thenReturn(null);

        // 10 de payer cost total = + 1 pref = 9 adicionales
        when(payerCost.getTotalAmount()).thenReturn(BigDecimal.TEN);

        assertEquals(new BigDecimal("9"), amountService.getAppliedCharges(PaymentTypes.CREDIT_CARD, payerCost));
    }

    @Test
    public void whenGetAmountToPayChargesAndCardChargesReturnCardTotal() {
        when(payerCost.getTotalAmount()).thenReturn(BigDecimal.TEN);

        assertEquals(BigDecimal.TEN, amountService.getAmountToPay(PaymentTypes.CREDIT_CARD, payerCost));
    }

    @Test
    public void whenGetAmountWithoutDiscountWithNoPayerCostReturnAmountWithoutDiscount() {
        when(amountConfiguration.getNoDiscountAmount()).thenReturn(BigDecimal.TEN);
        when(amountConfigurationRepository.getCurrentConfiguration()).thenReturn(amountConfiguration);
        assertEquals(BigDecimal.TEN, amountService.getAmountWithoutDiscount(PaymentTypes.ACCOUNT_MONEY, null));
    }

    @Test
    public void whenGetTaxFreeAmountWithNoPayerCostReturnTaxFreeAmount() {
        when(amountConfiguration.getTaxFreeAmount()).thenReturn(BigDecimal.TEN);
        when(amountConfigurationRepository.getCurrentConfiguration()).thenReturn(amountConfiguration);
        assertEquals(BigDecimal.TEN, amountService.getTaxFreeAmount(PaymentTypes.ACCOUNT_MONEY, null));
    }

    @Test
    public void whenGetNoDiscountAmountWithNoPayerCostThrowsExceptionThenItFallbacksToItemsPlusCharges() {
        when(checkoutPreference.getTotalAmount()).thenReturn(BigDecimal.ONE);
        when(chargeRepository.getChargeAmount(PaymentTypes.TICKET)).thenReturn(BigDecimal.TEN);
        when(amountConfigurationRepository.getCurrentConfiguration()).thenThrow(new IllegalStateException("Test ex"));
        amountService = Mockito.spy(amountService);
        final BigDecimal result = amountService.getAmountWithoutDiscount(PaymentTypes.TICKET, null);
        assertEquals(result, BigDecimal.TEN.add(BigDecimal.ONE));
        verify(amountService).getItemsPlusCharges(PaymentTypes.TICKET);
    }

    @Test
    public void whenGetTaxFreeAmountWithNoPayerCostThrowsExceptionThenItFallbacksToItemsPlusCharges() {
        when(checkoutPreference.getTotalAmount()).thenReturn(BigDecimal.ONE);
        when(chargeRepository.getChargeAmount(PaymentTypes.TICKET)).thenReturn(BigDecimal.TEN);
        when(amountConfigurationRepository.getCurrentConfiguration()).thenThrow(new IllegalStateException("Test ex"));
        amountService = Mockito.spy(amountService);
        final BigDecimal result = amountService.getTaxFreeAmount(PaymentTypes.TICKET, null);
        assertEquals(result, BigDecimal.TEN.add(BigDecimal.ONE));
        verify(amountService).getItemsPlusCharges(PaymentTypes.TICKET);
    }
}