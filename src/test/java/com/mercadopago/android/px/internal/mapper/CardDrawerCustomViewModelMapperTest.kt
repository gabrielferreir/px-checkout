package com.mercadopago.android.px.internal.mapper

import com.meli.android.carddrawer.model.customview.SwitchModel
import com.mercadopago.android.px.internal.mappers.CardDrawerCustomViewModelMapper
import com.mercadopago.android.px.model.internal.CardDrawerSwitch
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CardDrawerCustomViewModelMapperTest {

    @Mock
    private lateinit var cardDrawerSwitch: CardDrawerSwitch

    @Mock
    private lateinit var description: CardDrawerSwitch.Text

    @Mock
    private lateinit var accountMoneyOption: CardDrawerSwitch.Option

    @Mock
    private lateinit var creditCardOption: CardDrawerSwitch.Option

    @Mock
    private lateinit var states: CardDrawerSwitch.SwitchStates

    @Mock
    private lateinit var checkedState: CardDrawerSwitch.SwitchStates.State

    @Mock
    private lateinit var uncheckedState: CardDrawerSwitch.SwitchStates.State

    private val expectedSwitchModel = createModel()

    @Before
    fun setUp() {
        whenever(cardDrawerSwitch.safeZoneBackgroundColor).thenReturn(expectedSwitchModel.safeZoneBackgroundColor)
        whenever(cardDrawerSwitch.pillBackgroundColor).thenReturn(expectedSwitchModel.pillBackgroundColor)
        whenever(cardDrawerSwitch.switchBackgroundColor).thenReturn(expectedSwitchModel.switchBackgroundColor)
        whenever(cardDrawerSwitch.description).thenReturn(description)
        whenever(cardDrawerSwitch.states).thenAnswer {
            states
        }
        whenever(description.message).thenReturn(expectedSwitchModel.description.text)
        whenever(description.textColor).thenReturn(expectedSwitchModel.description.textColor)
        whenever(description.weight).thenReturn(expectedSwitchModel.description.weight)
        whenever(cardDrawerSwitch.options).thenReturn(listOf(accountMoneyOption, creditCardOption))

        whenever(accountMoneyOption.id).thenReturn(expectedSwitchModel.options.first().id)
        whenever(accountMoneyOption.name).thenReturn(expectedSwitchModel.options.first().name)

        whenever(creditCardOption.id).thenReturn(expectedSwitchModel.options.last().id)
        whenever(creditCardOption.name).thenReturn(expectedSwitchModel.options.last().name)

        whenever(states.checked).thenReturn(checkedState)
        whenever(checkedState.textColor).thenReturn(expectedSwitchModel.states.checkedState.textColor)
        whenever(checkedState.weight).thenReturn(expectedSwitchModel.states.checkedState.weight)

        whenever(states.unchecked).thenReturn(uncheckedState)
        whenever(uncheckedState.textColor).thenReturn(expectedSwitchModel.states.uncheckedState.textColor)
        whenever(uncheckedState.weight).thenReturn(expectedSwitchModel.states.uncheckedState.weight)
    }

    @Test
    fun whenMapCardDrawerSwitchToSwitchModelWithDefaultAccountMoney() {
        val actualSwitchModel = CardDrawerCustomViewModelMapper.mapToSwitchModel(cardDrawerSwitch, "account_money")

        Assert.assertTrue(ReflectionEquals(expectedSwitchModel).matches(actualSwitchModel))
    }

    private fun createModel():  SwitchModel {
        val states = SwitchModel.SwitchStates(
            SwitchModel.SwitchStates.State("#8c8c8c", "semi_bold"),
            SwitchModel.SwitchStates.State("#ffffff", "semi_bold")
        )
        val options = arrayListOf(
            SwitchModel.SwitchOption("account_money", "Débito"),
            SwitchModel.SwitchOption("credit_card", "Crédito")
        )
        val description = SwitchModel.Text(
            "#ffffff",
            "semi_bold",
            "Você paga com"
        )
        return SwitchModel(
            description,
            states,
            options,
            "#a6000000",
            "#ffffff",
            "#26000000",
            "account_money")
    }
}