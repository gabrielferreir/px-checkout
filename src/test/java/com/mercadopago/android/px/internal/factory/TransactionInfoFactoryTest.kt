package com.mercadopago.android.px.internal.factory

import com.mercadopago.android.px.internal.datasource.PayerPaymentMethodRepositoryImpl
import com.mercadopago.android.px.internal.datasource.TransactionInfoFactory
import com.mercadopago.android.px.model.CustomSearchItem
import com.mercadopago.android.px.model.FinancialInstitution
import com.mercadopago.android.px.model.PaymentMethod
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val DEBIN_PAYMENT_METHOD_ID = "debin_transfer"
private const val DEBIN_ACCOUNT_ID = "b1e3310e-0000-0000-0000-0cbf55f39b45"
private const val DEBIN_FINANCIAL_INSTITUTION_ID = "1"

private const val CARD_PAYMENT_METHOD_ID = "debvisa"
private const val CARD_ID = "2222222"

@RunWith(MockitoJUnitRunner::class)
class TransactionInfoFactoryTest {

    @MockK
    private lateinit var payerPaymentMethodRepository: PayerPaymentMethodRepositoryImpl
    @MockK
    private lateinit var debinPaymentMethod: PaymentMethod
    @MockK
    private lateinit var paymentMethod: PaymentMethod
    @MockK
    private lateinit var customSearchItem: CustomSearchItem
    @MockK
    private lateinit var financialInstitution: FinancialInstitution

    private lateinit var transactionInfoFactory: TransactionInfoFactory

    @Before
    fun `set up`() {
        MockKAnnotations.init(this)

        transactionInfoFactory = TransactionInfoFactory(payerPaymentMethodRepository)
    }

    @Test
    fun `when payment method is DEBIN then return valid bank info and financial institution id`() {

        every { financialInstitution.id } returns DEBIN_FINANCIAL_INSTITUTION_ID

        every { debinPaymentMethod.id } returns DEBIN_PAYMENT_METHOD_ID
        every { debinPaymentMethod.financialInstitutions } returns listOf(financialInstitution)

        every { customSearchItem.id } returns DEBIN_ACCOUNT_ID

        every { payerPaymentMethodRepository[any<String>()] } returns customSearchItem

        val transactionInfo = transactionInfoFactory.create(DEBIN_ACCOUNT_ID, debinPaymentMethod)

        assertNotNull(transactionInfo.bankInfo)
        assertNotNull(transactionInfo.financialInstitutionId)

        assertEquals(transactionInfo.bankInfo!!.accountId, DEBIN_ACCOUNT_ID)
        assertEquals(transactionInfo.financialInstitutionId!!, DEBIN_FINANCIAL_INSTITUTION_ID)
    }

    @Test
    fun `when payment method is not DEBIN then return empty transaction info`() {

        every { paymentMethod.id } returns CARD_PAYMENT_METHOD_ID
        every { paymentMethod.financialInstitutions } returns emptyList()

        every { customSearchItem.id } returns CARD_ID

        every { payerPaymentMethodRepository[any<String>()] } returns customSearchItem

        val transactionInfo = transactionInfoFactory.create(DEBIN_ACCOUNT_ID, paymentMethod)

        assertNull(transactionInfo.bankInfo)
        assertNull(transactionInfo.financialInstitutionId)
    }

    @Test(expected = IllegalStateException::class)
    fun `when payment method is DEBIN but account id is empty then throw exception`() {

        every { paymentMethod.id } returns DEBIN_PAYMENT_METHOD_ID
        every { paymentMethod.financialInstitutions } returns emptyList()

        every { customSearchItem.id } returns ""

        every { payerPaymentMethodRepository[any<String>()] } returns customSearchItem

        transactionInfoFactory.create(DEBIN_PAYMENT_METHOD_ID, paymentMethod)
    }

    @Test(expected = IllegalStateException::class)
    fun `when payment method is DEBIN but financial institution list is empty then throw exception`() {

        every { paymentMethod.id } returns DEBIN_PAYMENT_METHOD_ID
        every { paymentMethod.financialInstitutions } returns emptyList()

        every { customSearchItem.id } returns DEBIN_ACCOUNT_ID

        every { payerPaymentMethodRepository[any<String>()] } returns customSearchItem

        transactionInfoFactory.create(DEBIN_ACCOUNT_ID, paymentMethod)
    }
}