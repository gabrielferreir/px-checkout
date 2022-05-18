package com.mercadopago.android.px.internal.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.Size
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.presentation.extensions.loadOrElse
import com.mercadopago.android.px.internal.features.payment_congrats.adapter.PaymentCongratsTextAdapter
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText

internal class PaymentResultMethod @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var icon: ImageView
    private var amount: PaymentResultAmount
    private var details: AdapterLinearLayout
    private var extraInfo: AdapterLinearLayout

    init {
        inflate(context, R.layout.px_payment_result_method, this)
        icon = findViewById(R.id.icon)
        amount = findViewById(R.id.amount)
        details = findViewById(R.id.details)
        extraInfo = findViewById(R.id.extra_info)
    }

    fun setModel(model: Model) {
        icon.loadOrElse(model.imageUrl, R.drawable.px_generic_method)
        amount.setModel(model.amountModel)
        details.setAdapter(PaymentCongratsTextAdapter(context, model.details))
        model.extraInfo?.let {
            extraInfo.isVisible = true
            extraInfo.setAdapter(PaymentCongratsTextAdapter(context, it))
        }
    }

    class Model internal constructor(builder: Builder) {
        val imageUrl: String? = builder.imageUrl
        val amountModel: PaymentResultAmount.Model = builder.amountModel
        val details: List<PaymentCongratsText> = builder.details
        val extraInfo: List<PaymentCongratsText>? = builder.extraInfo

        class Builder(
            var amountModel: PaymentResultAmount.Model,
            @Size(min = 1) var details: List<PaymentCongratsText>
        ) {
            var extraInfo: List<PaymentCongratsText>? = null
            var imageUrl: String? = null

            fun setExtraInfo(extraInfo: List<PaymentCongratsText>) = apply { this.extraInfo = extraInfo }

            fun setImageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }

            fun build(): Model = Model(this)
        }
    }
}
