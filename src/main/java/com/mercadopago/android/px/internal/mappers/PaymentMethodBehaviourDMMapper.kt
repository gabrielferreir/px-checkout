package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.configuration.ModalContent
import com.mercadopago.android.px.configuration.Button
import com.mercadopago.android.px.configuration.Text
import com.mercadopago.android.px.configuration.PaymentMethodBehaviour
import com.mercadopago.android.px.model.internal.ButtonDM
import com.mercadopago.android.px.model.internal.PaymentMethodBehaviourDM
import com.mercadopago.android.px.model.internal.BehaviourDM
import com.mercadopago.android.px.model.internal.ModalContentDM
import com.mercadopago.android.px.model.internal.TextDM

internal class PaymentMethodBehaviourDMMapper : Mapper<PaymentMethodBehaviour, PaymentMethodBehaviourDM>() {

    override fun map(value: PaymentMethodBehaviour): PaymentMethodBehaviourDM {

        val behaviours = value.behaviours?.map { behaviour ->
            behaviour.modalContent.button
            BehaviourDM(
                behaviour.type,
                modalContentToMDM(behaviour.modalContent)
            )
        }

        return PaymentMethodBehaviourDM(
            value.paymentTypeRules.orEmpty(),
            value.paymentMethodRules.orEmpty(),
            value.sliderTitle,
            behaviours.orEmpty()
        )
    }

    private fun modalContentToMDM(modal: ModalContent): ModalContentDM {
        return ModalContentDM(
            modal.title?.let(::textToDM),
            modal.description.let(::textToDM),
            modal.button.let(::buttonToDM),
            modal.imageUrl
        )
    }

    private fun textToDM(text: Text): TextDM {
        return TextDM(
            text.message,
            text.backgroundColor,
            text.textColor,
            text.weight
        )
    }

    private fun buttonToDM(button: Button): ButtonDM {
        return ButtonDM(
            button.label,
            button.target
        )
    }
}
