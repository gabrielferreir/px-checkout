package com.mercadopago.android.px.internal.features.payment_result.remedies.view

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.mercadopago.android.px.R

internal class CvvRemedy(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        LinearLayout(context, attrs, defStyleAttr) {

    init {
        configureView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    var listener: Listener? = null
    private var previousCvv = ""
    private var length: Int = 3
    private lateinit var input: TextInputEditText
    private lateinit var inputContainer: TextInputLayout
    private lateinit var info: TextView

    private fun configureView(context: Context) {
        orientation = VERTICAL
        inflate(context, R.layout.px_remedies_cvv, this)
        input = findViewById(R.id.input)
        inputContainer = findViewById(R.id.input_container)
        info = findViewById(R.id.info)
        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                previousCvv = text.toString()
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateCvvFilledStatus(text)
            }
        })
    }

    fun init(model: Model) {
        input.filters = arrayOf(InputFilter.LengthFilter(model.length))
        inputContainer.hint = model.hint
        info.text = model.info
        length = model.length
        updateCvvFilledStatus(input.text)
    }

    private fun updateCvvFilledStatus(text: CharSequence?) {
        if (text?.length == length) {
            listener?.onCvvFilled(text.toString())
        } else if (previousCvv.length == length || previousCvv.isEmpty()) {
            listener?.onCvvDeleted()
        }
    }

    interface Listener {
        fun onCvvFilled(cvv: String)
        fun onCvvDeleted()
    }

    internal data class Model(val hint: String, val info: String, val length: Int) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readInt())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(hint)
            parcel.writeString(info)
            parcel.writeInt(length)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<Model> {
            override fun createFromParcel(parcel: Parcel) = Model(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Model>(size)
        }
    }
}
