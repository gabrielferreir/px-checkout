package com.mercadopago.android.px.model

internal data class TermsAndConditionsLinks(
    private val mapLinks: Map<String, String>,
    private val mapInstallments: Map<String, String>,
    private val defaultLink: String
) {
    fun getLinkByInstallment(installment: Int): String {
        val key = installment.toString()
        return mapLinks.getOrElse(mapInstallments[key].orEmpty(), { defaultLink })
    }
}
