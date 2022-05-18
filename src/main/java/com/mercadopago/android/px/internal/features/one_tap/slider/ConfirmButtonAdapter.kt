package com.mercadopago.android.px.internal.features.one_tap.slider

import android.view.View
import com.mercadopago.android.px.internal.features.one_tap.confirm_button.ConfirmButton
import com.mercadopago.android.px.internal.viewmodel.ConfirmButtonViewModel
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState
import com.mercadopago.android.px.model.internal.Application

internal class ConfirmButtonAdapter(
    private val confirmButton: ConfirmButton.View
) : HubableAdapter<List<ConfirmButtonViewModel.ByApplication>, View>(null) {

    override fun updateData(
        currentIndex: Int,
        payerCostSelected: Int,
        splitSelectionState: SplitSelectionState,
        application: Application
    ) {

        data?.let {
            if (it[currentIndex][application].isDisabled) {
                confirmButton.disable()
            } else {
                confirmButton.enable()
            }
        }
    }

    override fun getNewModels(model: HubAdapter.Model): List<ConfirmButtonViewModel.ByApplication> {
        return model.confirmButtonViewModels
    }
}