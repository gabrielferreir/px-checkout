package com.mercadopago.android.px.model.commission

import android.os.Parcel
import android.os.Parcelable
import com.mercadopago.android.px.core.DynamicDialogCreator
import com.mercadopago.android.px.internal.extensions.isZero
import com.mercadopago.android.px.internal.repository.ChargeRepository
import com.mercadopago.android.px.internal.util.ParcelableUtil
import java.io.Serializable
import java.math.BigDecimal

class PaymentTypeChargeRule private constructor(
    val paymentTypeId: String,
    private val charge: BigDecimal,
    val detailModal: DynamicDialogCreator?,
    val message: String?,
    val label: String?,
    val taxable: Boolean = true
) : Serializable, Parcelable {

    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        ParcelableUtil.getBigDecimal(parcel),
        parcel.readParcelable(DynamicDialogCreator::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as Boolean
    )

    internal constructor(
        paymentTypeId: String,
        charge: BigDecimal,
        detailModal: DynamicDialogCreator?,
        label: String?
    ) : this(paymentTypeId, charge, detailModal, null, label)

    /**
     * Deprecated: Use [Builder] instead
     *
     * @param paymentTypeId the payment type associated with the charge to shouldBeTriggered.
     * @param charge the charge amount to apply for this rule
     * @param detailModal creator for the dialog with charge info
     *
     */
    @JvmOverloads
    @Deprecated("In favor of builder")
    constructor(
        paymentTypeId: String, charge: BigDecimal,
        detailModal: DynamicDialogCreator? = null
    ) : this(paymentTypeId, charge, detailModal, null, null)

    //Shouldn't really exist
    @Deprecated("")
    fun shouldBeTriggered(chargeRepository: ChargeRepository) = false

    fun hasDetailModal() = detailModal != null

    fun charge() = charge

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(paymentTypeId)
        ParcelableUtil.write(parcel, charge)
        parcel.writeParcelable(detailModal, flags)
        parcel.writeString(message)
        parcel.writeString(label)
        parcel.writeValue(taxable)
    }

    override fun describeContents() = 0

    /**
     * PaymentTypeChargeRule builder that allows you to create a [PaymentTypeChargeRule]
     *
     * @param paymentTypeId the payment type id to apply the charge to
     * @param amount the amount for the charge (must not be zero, for charge free rules use [createChargeFreeRule])
     */
    class Builder(val paymentTypeId: String, val amount: BigDecimal) {
        private var label: String? = null
        private var detailModal: DynamicDialogCreator? = null
        private var taxable: Boolean = true

        init {
            require(!amount.isZero())
        }

        fun setTaxable(taxable: Boolean) = apply { this.taxable = taxable }

        fun setLabel(label: String) = apply { this.label = label }

        fun setDetailModal(detailModal: DynamicDialogCreator?) = apply { this.detailModal = detailModal }

        fun build(): PaymentTypeChargeRule {
            return PaymentTypeChargeRule(paymentTypeId, amount, detailModal, null, label, taxable)
        }
    }

    companion object {
        /**
         * Factory method to create a charge free rule, used to highlight payment types without charges.
         * @param paymentTypeId payment type without charges
         * @param message message which will be shown in the highlighted payment type
         * @return
         */
        @JvmStatic
        fun createChargeFreeRule(paymentTypeId: String, message: String): PaymentTypeChargeRule {
            return PaymentTypeChargeRule(paymentTypeId, BigDecimal.ZERO, null, message, null)
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<PaymentTypeChargeRule> {
            override fun createFromParcel(parcel: Parcel) = PaymentTypeChargeRule(parcel)
            override fun newArray(size: Int) = arrayOfNulls<PaymentTypeChargeRule>(size)
        }
    }
}