package com.mercadopago.android.px.internal.mappers

import com.meli.android.carddrawer.model.customview.SwitchModel
import com.mercadopago.android.px.model.internal.CardDrawerSwitch

internal object CardDrawerCustomViewModelMapper {

    fun mapToSwitchModel(cardDrawerSwitch: CardDrawerSwitch?, default: String) = cardDrawerSwitch?.run {
        val states = SwitchModel.SwitchStates(
            getStateForCardDrawer(states.checked),
            getStateForCardDrawer(states.unchecked)
        )
        val description = SwitchModel.Text(description.textColor, description.weight, description.message)
        SwitchModel(
            description,
            states,
            getOptionsForCardDrawer(options),
            switchBackgroundColor,
            pillBackgroundColor,
            safeZoneBackgroundColor,
            default)
    }

    private fun getStateForCardDrawer(state: CardDrawerSwitch.SwitchStates.State) = state
        .run { SwitchModel.SwitchStates.State(textColor, weight) }

    private fun getOptionsForCardDrawer(options: List<CardDrawerSwitch.Option>) = options
        .map { SwitchModel.SwitchOption(it.id, it.name) }
}