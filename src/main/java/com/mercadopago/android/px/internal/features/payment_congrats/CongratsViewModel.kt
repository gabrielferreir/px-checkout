package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.internal.base.BaseState
import com.mercadopago.android.px.internal.base.BaseViewModelWithState
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.datasource.PaymentResultFactory
import com.mercadopago.android.px.internal.features.checkout.PostCongratsDriver
import com.mercadopago.android.px.internal.features.checkout.PostPaymentUrlsMapper
import com.mercadopago.android.px.internal.livedata.MediatorSingleLiveData
import com.mercadopago.android.px.internal.repository.CongratsRepository
import com.mercadopago.android.px.internal.repository.PaymentRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.Payment
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import com.mercadopago.android.px.tracking.internal.events.NoConnectionFrictionTracker
import kotlinx.android.parcel.Parcelize

internal class CongratsViewModel(
    private val congratsRepository: CongratsRepository,
    private val paymentRepository: PaymentRepository,
    private val congratsResultFactory: CongratsResultFactory,
    private val connectionHelper: ConnectionHelper,
    private val paymentSettingRepository: PaymentSettingRepository,
    private val postPaymentUrlsMapper: PostPaymentUrlsMapper,
    private val paymentResultFactory: PaymentResultFactory,
    tracker: MPTracker
) : BaseViewModelWithState<CongratsViewModel.State>(tracker), CongratsRepository.PostPaymentCallback {

    val congratsResultLiveData = MediatorSingleLiveData<CongratsResult>()
    val postPaymentUrlsLiveData = MediatorSingleLiveData<CongratsPostPaymentUrlsResponse>()
    val exitFlowLiveData = MediatorSingleLiveData<CongratsPostPaymentUrlsResponse>()

    override fun initState() = State()

    fun createCongratsResult(iPaymentDescriptor: IPaymentDescriptor?) {
        congratsResultLiveData.value = CongratsPostPaymentResult.Loading

        if (connectionHelper.hasConnection()) {
            val descriptor = iPaymentDescriptor ?: paymentRepository.payment
            if (descriptor != null) {
                runCatching {
                    val paymentResult = paymentResultFactory.create(descriptor)
                    congratsRepository.getPostPaymentData(descriptor, paymentResult, this@CongratsViewModel)
                }.onFailure {
                    track(
                        FrictionEventTracker.with(
                            "${TrackWrapper.BASE_PATH}/post_payment_create_result",
                            FrictionEventTracker.Id.INVALID_POST_PAYMENT_CREATE_RESULT,
                            FrictionEventTracker.Style.NON_SCREEN,
                            MercadoPagoError.createNotRecoverable(it.message.orEmpty())
                        )
                    )
                    congratsResultLiveData.value = CongratsPostPaymentResult.BusinessError()
                }
            } else {
                congratsResultLiveData.value = CongratsPostPaymentResult.BusinessError()
            }
        } else {
            manageNoConnection()
        }
    }

    override fun handleResult(paymentModel: PaymentModel) {
        val postPaymentUrls = resolvePostPaymentUrls(paymentModel)
        state.backUrl = postPaymentUrls?.backUrl.orEmpty()
        state.redirectUrl = postPaymentUrls?.redirectUrl.orEmpty()
        congratsResultLiveData.value = congratsResultFactory.create(paymentModel, state.redirectUrl)
    }

    private fun resolvePostPaymentUrls(paymentModel: PaymentModel): PostPaymentUrlsMapper.Response? {
        return paymentSettingRepository.checkoutPreference?.let { preference ->
            val congratsResponse = paymentModel.congratsResponse
            postPaymentUrlsMapper.map(
                PostPaymentUrlsMapper.Model(
                    congratsResponse.redirectUrl,
                    congratsResponse.backUrl,
                    paymentModel.payment,
                    preference,
                    paymentSettingRepository.site.id
                )
            )
        }
    }

    private fun manageNoConnection() {
        track(NoConnectionFrictionTracker)
        congratsResultLiveData.value = CongratsPostPaymentResult.ConnectionError
    }

    fun onPaymentResultResponse(customResultCode: Int?) {
        PostCongratsDriver.Builder(
            state.iPaymentDescriptor,
            PostPaymentUrlsMapper.Response(state.redirectUrl, state.backUrl)
        )
            .customResponseCode(customResultCode)
            .action(object : PostCongratsDriver.Action {
                override fun goToLink(link: String) {
                    postPaymentUrlsLiveData.value = CongratsPostPaymentUrlsResponse.OnGoToLink(link)
                }

                override fun openInWebView(link: String) {
                    postPaymentUrlsLiveData.value = CongratsPostPaymentUrlsResponse.OnOpenInWebView(link)
                }

                override fun exitWith(customResponseCode: Int?, payment: Payment?) {
                    exitFlowLiveData.value =
                        CongratsPostPaymentUrlsResponse.OnExitWith(customResponseCode, payment)
                }
            }).build().execute()
    }

    @Parcelize
    data class State(
        var iPaymentDescriptor: IPaymentDescriptor? = null,
        var backUrl: String? = null,
        var redirectUrl: String? = null
    ) : BaseState
}
