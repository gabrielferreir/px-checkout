package com.mercadopago.android.px.internal.features.one_tap.confirm_button

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.mercadopago.android.px.internal.base.BaseState
import com.mercadopago.android.px.internal.base.BaseViewModelWithState
import com.mercadopago.android.px.internal.features.pay_button.ConfirmButtonUiState
import com.mercadopago.android.px.internal.mappers.PayButtonViewModelMapper
import com.mercadopago.android.px.internal.repository.CustomTextsRepository
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.ConfirmButtonPressedEvent
import java.lang.ref.WeakReference
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel as ButtonConfig

internal abstract class ConfirmButtonViewModel<S : BaseState, H : ConfirmButton.Handler>(
    customTextsRepository: CustomTextsRepository,
    payButtonViewModelMapper: PayButtonViewModelMapper,
    tracker: MPTracker
) : BaseViewModelWithState<S>(tracker), ConfirmButton.ViewModel {

    private val buttonTextMutableLiveData = MutableLiveData<ButtonConfig>()
    val buttonTextLiveData: LiveData<ButtonConfig>
        get() = buttonTextMutableLiveData
    protected val uiStateMutableLiveData = MediatorLiveData<ConfirmButtonUiState>()
    val uiStateLiveData: LiveData<ConfirmButtonUiState>
        get() = uiStateMutableLiveData

    protected var buttonConfig = payButtonViewModelMapper.map(customTextsRepository.customTexts)

    init {
        buttonTextMutableLiveData.value = buttonConfig
    }

    private lateinit var handlerReference: WeakReference<H>
    val handler: H
        get() = handlerReference.get()!!

    @CallSuper
    override fun onButtonPressed() {
        handler.getViewTrackPath(object : ConfirmButton.ViewTrackPathCallback {
            override fun call(viewTrackPath: String) {
                track(ConfirmButtonPressedEvent(viewTrackPath))
            }
        })
    }

    fun attach(handler: H) {
        handlerReference = WeakReference(handler)
    }

    fun detach() {
        handlerReference.clear()
    }
}
