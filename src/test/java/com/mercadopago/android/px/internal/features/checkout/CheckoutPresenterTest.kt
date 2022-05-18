package com.mercadopago.android.px.internal.features.checkout

import com.mercadopago.android.px.TestContextProvider
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.domain.CheckoutUseCase
import com.mercadopago.android.px.internal.domain.CheckoutWithNewCardUseCase
import com.mercadopago.android.px.internal.experiments.Variant
import com.mercadopago.android.px.internal.repository.CheckoutRepository
import com.mercadopago.android.px.internal.repository.ExperimentsRepository
import com.mercadopago.android.px.internal.repository.PaymentRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.internal.util.ApiUtil
import com.mercadopago.android.px.mocks.CheckoutResponseStub
import com.mercadopago.android.px.model.BusinessPayment
import com.mercadopago.android.px.model.Payment
import com.mercadopago.android.px.model.Site
import com.mercadopago.android.px.model.Sites
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.preferences.CheckoutPreference
import com.mercadopago.android.px.tracking.internal.MPTracker
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever


@RunWith(MockitoJUnitRunner::class)
class CheckoutPresenterTest {
    @Mock
    private lateinit var checkoutView: Checkout.View

    @Mock
    private lateinit var paymentSettingRepository: PaymentSettingRepository

    @Mock
    private lateinit var userSelectionRepository: UserSelectionRepository

    @Mock
    private lateinit var checkoutRepository: CheckoutRepository

    @Mock
    private lateinit var paymentRepository: PaymentRepository

    @Mock
    private lateinit var experimentsRepository: ExperimentsRepository

    @Mock
    private lateinit var postPaymentUrlsMapper: PostPaymentUrlsMapper

    @Mock
    private lateinit var postPaymentUrlsResponse: PostPaymentUrlsMapper.Response

    private lateinit var checkoutUseCase: CheckoutUseCase
    private lateinit var checkoutWithNewCardUseCase: CheckoutWithNewCardUseCase
    private lateinit var presenter: CheckoutPresenter

    @Before
    fun setUp() {
        val site = Mockito.mock(Site::class.java)
        whenever(site.id).thenReturn(Sites.ARGENTINA.id)
        whenever(paymentSettingRepository.site).thenReturn(site)
        whenever(paymentSettingRepository.checkoutPreference).thenReturn(
            Mockito.mock(
                CheckoutPreference::class.java
            )
        )
        whenever(postPaymentUrlsMapper.map(any<PostPaymentUrlsMapper.Model>()))
            .thenReturn(postPaymentUrlsResponse)
        checkoutUseCase =
            CheckoutUseCase(checkoutRepository, Mockito.mock(MPTracker::class.java), TestContextProvider())
        checkoutWithNewCardUseCase =
            CheckoutWithNewCardUseCase(
                checkoutRepository, Mockito.mock(MPTracker::class.java), TestContextProvider()
            )
        presenter = getPresenter()
    }

    @Test
    fun whenCheckoutInitializedAndPaymentMethodSearchFailsThenShowError() {
        runBlocking {
            val apiException = ApiException()
            whenever(checkoutRepository.checkout()).thenReturn(
                Response.Failure(MercadoPagoError(apiException, ApiUtil.RequestOrigin.POST_INIT)))
        }
        presenter.initialize()
        verify(checkoutView).showProgress()
        verify(checkoutView).showError(ArgumentMatchers.any(MercadoPagoError::class.java))
        verifyNoMoreInteractions(checkoutView)
    }

    @Test
    fun whenChoHasPreferenceAndPaymentMethodRetrievedShowOneTap() {
        val checkoutResponse = CheckoutResponseStub.FULL.get()
        runBlocking {
            whenever(checkoutRepository.checkout())
                .thenReturn(Response.Success(checkoutResponse))
        }

        presenter.initialize()
        runBlocking {
            verify(checkoutRepository).checkout()
            verify(checkoutRepository).configure(checkoutResponse)
        }
        verify(checkoutView).showProgress()
        verify(checkoutView).hideProgress()
        verify(checkoutView).showOneTap(ArgumentMatchers.any(Variant::class.java))
        verifyNoMoreInteractions(checkoutView)
        verifyNoMoreInteractions(checkoutRepository)
    }

    @Test
    fun whenPaymentResultWithCreatedPaymentThenFinishCheckoutWithPaymentResult() {
        val payment = Mockito.mock(Payment::class.java)
        whenever(paymentRepository.payment).thenReturn(payment)
        presenter.onPaymentResultResponse(null, null, null)
        verify(checkoutView).finishWithPaymentResult(null, payment)
    }

    @Test
    fun whenPaymentResultWithoutCreatedPaymentThenFinishCheckoutWithoutPaymentResult() {
        presenter.onPaymentResultResponse(null, null, null)
        verify(checkoutView).finishWithPaymentResult(null, null)
    }

    @Test
    fun whenErrorShownAndValidIdentificationThenCancelCheckout() {
        val apiException = Mockito.mock(
            ApiException::class.java
        )
        val mpException = MercadoPagoError(apiException, "")
        presenter.onErrorCancel(mpException)
        verify(checkoutView).cancelCheckout()
        verifyNoMoreInteractions(checkoutView)
    }

    @Test
    fun whenCustomPaymentResultResponseHasPaymentThenFinishWithPaymentResult() {
        val payment = Mockito.mock(Payment::class.java)
        whenever(paymentRepository.payment).thenReturn(payment)
        presenter.onPaymentResultResponse(CUSTOM_RESULT_CODE, null, null)
        verify(checkoutView)
            .finishWithPaymentResult(CUSTOM_RESULT_CODE, payment)
        verifyNoMoreInteractions(checkoutView)
    }

    @Test
    fun whenCustomPaymentResultResponseHasNotPaymentThenFinishWithPaymentResult() {
        presenter.onPaymentResultResponse(CUSTOM_RESULT_CODE, null, null)
        verify(checkoutView)
            .finishWithPaymentResult(CUSTOM_RESULT_CODE, null)
        verifyNoMoreInteractions(checkoutView)
    }

    @Test
    fun whenCustomPaymentResultResponseHasBusinessPaymentThenFinishWithPaymentResult() {
        val payment = Mockito.mock(BusinessPayment::class.java)
        whenever(paymentRepository.payment).thenReturn(payment)
        presenter.onPaymentResultResponse(CUSTOM_RESULT_CODE, null, null)
        verify(checkoutView)
            .finishWithPaymentResult(CUSTOM_RESULT_CODE, null)
        verifyNoMoreInteractions(checkoutView)
    }

    // --------- Helper methods ----------- //
    private fun getBasePresenter(view: Checkout.View?): CheckoutPresenter {
        presenter = CheckoutPresenter(
            paymentSettingRepository, userSelectionRepository,
            checkoutUseCase, checkoutWithNewCardUseCase, paymentRepository, experimentsRepository, postPaymentUrlsMapper, Mockito.mock(
                MPTracker::class.java
            ),
            false
        )
        presenter.attachView(view)
        return presenter
    }

    private fun getPresenter(): CheckoutPresenter {
        return getBasePresenter(checkoutView)
    }

    companion object {
        const val CUSTOM_RESULT_CODE = 1
    }
}
