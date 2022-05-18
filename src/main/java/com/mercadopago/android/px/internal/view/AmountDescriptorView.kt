package com.mercadopago.android.px.internal.view

import android.content.Context
import android.content.res.Configuration
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.mercadolibre.android.picassodiskcache.PicassoDiskLoader.get
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.DynamicDialogCreator
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.util.executeIfAccessibilityTalkBackEnable
import com.mercadopago.android.px.internal.viewmodel.IDetailColor
import com.mercadopago.android.px.internal.viewmodel.IDetailDrawable
import com.mercadopago.android.px.internal.viewmodel.SummaryRowIconDescriptor
import com.mercadopago.android.px.internal.viewmodel.SummaryRowTextDescriptor
import com.mercadopago.android.px.model.DiscountConfigurationModel

internal class AmountDescriptorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    //region Views
    private val label: MPTextView
    private var labelIcon: ImageView
    private var labelContainer: View
    private val brief: MPTextView
    private var amount: MPTextView
    //endregion

    //region Animations
    private val slideLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.px_summary_slide_left_in)
    private val slideRightAnimation = AnimationUtils.loadAnimation(context, R.anim.px_summary_slide_right_in)
    //endregion

    init {
        inflate(getContext(), R.layout.px_view_amount_descriptor, this)
        labelContainer = findViewById<View>(R.id.descriptor_container)
        label = labelContainer.findViewById(R.id.descriptor)
        brief = labelContainer.findViewById(R.id.brief)
        amount = findViewById(R.id.amount)
        labelIcon = findViewById(R.id.icon_descriptor)
    }

    fun animateEnter() {
        labelContainer.startAnimation(slideRightAnimation)
        amount.startAnimation(slideLeftAnimation)
    }

    fun update(model: Model) {
        updateLabelText(model)
        updateLabelBriefText(model)
        updateLabelDrawable(model)
        updateAmountText(model)
        setOnClickListener(model.label.iconDescriptor?.listener)
        executeIfAccessibilityTalkBackEnable(context) {
            addContentDescription(model)
        }
    }

    private fun updateAmountText(model: Model) {
        amount.loadOrGone(model.amount)
    }

    private fun updateLabelDrawable(model: Model) {
        labelIcon.visibility = if (model.label.iconDescriptor != null) VISIBLE else INVISIBLE
        model.label.iconDescriptor?.let {
            updateDrawable(it.drawable, it.drawableColor)
        }
    }

    private fun updateLabelText(model: Model) {
        label.loadTextListOrGone(model.label.textDescriptor)
    }

    private fun updateLabelBriefText(model: Model) {
        if ((model.label.shouldShowBrief || ViewUtils.isScreenSize(context, Configuration.SCREENLAYOUT_SIZE_LARGE))) {
            brief.loadTextListOrGone(model.label.briefTextDescriptor)
        } else {
            brief.visibility = GONE
        }
    }

    private fun addContentDescription(model: Model) {
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(label.text).append(TextUtil.SPACE)
        val textAmount = model.amount.text.get(context)
        if (textAmount.isNotEmpty()) {
            spannableStringBuilder
                .append(textAmount.split(TextUtil.SPACE).last())
                .append(resources.getString(R.string.px_money))
        }
        contentDescription = spannableStringBuilder.toString()
    }

    private fun updateDrawable(detailDrawable: IDetailDrawable, detailColor: IDetailColor) {
        detailDrawable.let { labelIcon.setImageDrawable(it.getDrawable(context)) }
        detailColor.let { labelIcon.setColorFilter(it.getColor(context)) }
    }

    interface OnClickListener {
        fun onDiscountAmountDescriptorClicked(discountModel: DiscountConfigurationModel)
        fun onChargesAmountDescriptorClicked(dynamicDialogCreator: DynamicDialogCreator)
    }

    class Model @JvmOverloads constructor(
        val label: LabelDescriptor,
        val amount: SummaryRowTextDescriptor
    ) {
        data class LabelDescriptor(
            val textDescriptor: List<SummaryRowTextDescriptor>,
            val iconDescriptor: SummaryRowIconDescriptor? = null,
            val briefTextDescriptor: List<SummaryRowTextDescriptor>? = null,
            val shouldShowBrief: Boolean = true
        )
    }

    companion object {
        @JvmStatic
        fun getDesiredHeight(context: Context): Int {
            val view = inflate(context, R.layout.px_viewholder_amountdescription, null)
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            return view.measuredHeight
        }
    }
}