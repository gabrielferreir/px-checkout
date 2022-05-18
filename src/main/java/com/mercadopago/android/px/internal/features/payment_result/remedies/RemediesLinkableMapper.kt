package com.mercadopago.android.px.internal.features.payment_result.remedies

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.internal.viewmodel.LazyString
import com.mercadopago.android.px.model.LinkableText

typealias LinkableTextDisplayInfo = com.mercadopago.android.px.model.display_info.LinkableText

internal class RemediesLinkableMapper(val context: Context) : Mapper<LinkableTextDisplayInfo, LinkableText>() {

    override fun map(value: LinkableTextDisplayInfo): LinkableText {

        val linkablePhraselist: ArrayList<LinkableText.LinkablePhrase> = ArrayList()
        for (linkablePhrase in value.linkablePhrases) {
            val linkablePhraseRemedies = LinkableText.LinkablePhrase(
                linkablePhrase.phrase, linkablePhrase.textColor,
                linkablePhrase.link, linkablePhrase.html, linkablePhrase.installments
            )
            linkablePhraselist.add(linkablePhraseRemedies)
        }

        return LinkableText(
            value.text,
            value.textColor,
            linkablePhraselist,
            value.links
        )
    }

    @SuppressLint("ResourceType")
    fun mapRemedies(value: LinkableTextDisplayInfo): LinkableText {

        val linkablePhraselist: ArrayList<LinkableText.LinkablePhrase> = ArrayList()
        for (linkablePhrase in value.linkablePhrases) {
            val linkablePhraseRemedies = LinkableText.LinkablePhrase(
                linkablePhrase.phrase, R.color.px_remedies_link_blue.toString(),
                linkablePhrase.link, linkablePhrase.html, linkablePhrase.installments
            )
            linkablePhraselist.add(linkablePhraseRemedies)
        }

        return LinkableText(
            value.text,
            context.resources.getString(R.color.px_payment_result_component_remedies),
            linkablePhraselist,
            value.links
        )
    }
}