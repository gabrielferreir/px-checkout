package com.mercadopago.android.px.tracking.internal.model

internal data class ApplicationInfo(
    val paymentMethodId: String,
    val paymentTypeId: String,
    val enable: Boolean,
    val statusDetail: String?,
    val validationPrograms: List<ValidationProgramInfo>) : TrackingMapModel() {

    data class ValidationProgramInfo(
        val id: String,
        val mandatory: Boolean) : TrackingMapModel()
}