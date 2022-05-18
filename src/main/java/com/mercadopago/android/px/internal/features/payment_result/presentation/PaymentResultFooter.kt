package com.mercadopago.android.px.internal.features.payment_result.presentation

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.mercadolibre.android.andesui.button.AndesButton
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchy
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.extensions.gone
import com.mercadopago.android.px.internal.extensions.isNotNull
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.extensions.visible
import com.mercadopago.android.px.model.ExitAction
import kotlinx.android.parcel.Parcelize

internal class PaymentResultFooter(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {

    init {
        configureView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private lateinit var primaryButton: AndesButton
    private lateinit var secondaryButton: AndesButton
    private lateinit var payButtonContainer: View
    private lateinit var autoReturn: View
    private lateinit var autoReturnLabel: TextView
    private lateinit var separator: View

    private fun configureView(context: Context) {
        orientation = VERTICAL
        inflate(context, R.layout.px_payment_result_footer, this)
        primaryButton = findViewById(R.id.primary_button)
        secondaryButton = findViewById(R.id.secondary_button)
        payButtonContainer = findViewById(R.id.confirm_button_container)
        autoReturn = findViewById(R.id.auto_return)
        autoReturnLabel = autoReturn.findViewById(R.id.label)
        separator = findViewById(R.id.separator)
    }

    fun init(model: Model, listener: Listener) {
        applyButtonConfig(primaryButton, model.primaryButton, listener)
        applyButtonConfig(secondaryButton, model.secondaryButton, listener)
        payButtonContainer.visibility = if (model.showPayButton) View.VISIBLE else View.GONE
        if (model.primaryButton == null && model.secondaryButton.isNotNull() &&
            model.secondaryButton.type == PaymentResultButton.Type.TRANSPARENT) {
            separator.visible()
        } else {
            separator.gone()
        }
    }

    fun showAutoReturn() {
        autoReturn.visible()
    }

    fun updateAutoReturnLabel(label: String) {
        autoReturnLabel.text = label
    }

    fun hideSecondaryButton() {
        secondaryButton.gone()
    }

    fun showSecondaryButton() {
        secondaryButton.visible()
    }

    private fun applyButtonConfig(button: AndesButton, model: PaymentResultButton?, listener: Listener) {
        with(button) {
            model?.let { model ->
                visible()
                text = model.label.get(context).toString()
                hierarchy = AndesButtonHierarchy.fromString(model.type.name)
                setOnClickListener {
                    when {
                        model.exitAction.isNotNull() -> listener.onClick(model.exitAction)
                        model.target.isNotNullNorEmpty() -> listener.onClick(model.target)
                        model.action.isNotNull() -> listener.onClick(model.action)
                    }
                }
            } ?: gone()
        }
    }

    interface Listener {
        @JvmDefault fun onClick(action: PaymentResultButton.Action) = Unit
        @JvmDefault fun onClick(action: ExitAction) = Unit
        @JvmDefault fun onClick(target: String) = Unit
    }

    @Parcelize
    internal data class Model(
        val primaryButton: PaymentResultButton?,
        val secondaryButton: PaymentResultButton?,
        val showPayButton: Boolean = false
    ) : Parcelable
}
