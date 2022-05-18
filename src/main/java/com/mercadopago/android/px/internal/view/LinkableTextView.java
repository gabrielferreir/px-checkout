package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.core.presentation.extensions.SpannableExtKt;
import com.mercadopago.android.px.core.presentation.extensions.TextViewExtKt;
import com.mercadopago.android.px.model.TermsAndConditionsLinks;
import com.mercadopago.android.px.model.LinkableText;
import com.mercadopago.android.px.internal.util.TextUtil;
import java.util.Map;

public class LinkableTextView extends androidx.appcompat.widget.AppCompatTextView {

    private LinkableText model;
    LinkableTextView.LinkableTextListener listener = (termsAndConditionsLinks) -> {
    };

    public LinkableTextView(@NonNull final Context context, @NonNull final AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateModel(@Nullable final LinkableText model) {
        if (model != null) {
            this.model = model;
            render();
        }
    }

    public void setLinkableTextListener(@NonNull final LinkableTextView.LinkableTextListener listener) {
        this.listener = listener;
    }

    private void render() {
        if (TextUtil.isNotEmpty(model.getText())) {
            final Spannable spannableText = new SpannableStringBuilder(model.getText());
            for (final LinkableText.LinkablePhrase linkablePhrase : model.getLinkablePhrases()) {
                addLinkToSpannable(spannableText, linkablePhrase);
            }
            TextViewExtKt.setTextColor(this, model.getTextColor());
            setText(spannableText);
            setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void addLinkToSpannable(@NonNull final Spannable spannable,
        @NonNull final LinkableText.LinkablePhrase link) {
        final String phrase = link.getPhrase();
        final int start = TextUtil.isNotEmpty(phrase) ? model.getText().indexOf(phrase) : -1;
        if (start >= 0) {
            final int end = start + phrase.length();
            final TermsAndConditionsLinks installmentLink = buildTermsAndConditionsLinks(link);
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull final View widget) {
                    listener.onLinkClicked(installmentLink);
                }
            }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            SpannableExtKt.setColor(spannable, link.getTextColor(), start, end);
        }
    }

    private TermsAndConditionsLinks buildTermsAndConditionsLinks(
        @NonNull final LinkableText.LinkablePhrase linkablePhrase
    ) {
        final Map<String, String> links = model.getLinkMap();
        String link = "";

        if (linkablePhrase.getLink() != null || linkablePhrase.getHtml() != null) {
            link = linkablePhrase.getLink() != null ? linkablePhrase.getLink() : linkablePhrase.getHtml();
        }

        return new TermsAndConditionsLinks(links, linkablePhrase.getInstallmentMap(), link);
    }

    public interface LinkableTextListener {
        void onLinkClicked(@NonNull final TermsAndConditionsLinks termsAndConditionsLinks);
    }
}