package com.mercadopago.android.px.internal.base

import com.mercadopago.android.px.internal.livedata.MutableSingleLiveData
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction
import com.mercadopago.android.px.tracking.internal.MPTracker

class FragmentCommunicationViewModel(tracker: MPTracker) : BaseViewModel(tracker) {
    val postPaymentActionLiveData = MutableSingleLiveData<PostPaymentAction>()
}
