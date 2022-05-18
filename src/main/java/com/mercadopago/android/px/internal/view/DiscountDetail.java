package com.mercadopago.android.px.internal.view;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.TermsAndConditionsActivity;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.viewmodel.DiscountBody;
import com.mercadopago.android.px.model.TextUrl;

public class DiscountDetail {
    private final DiscountBody discountBody;

    /* default */ DiscountDetail(@NonNull final DiscountBody discountBody) {
        this.discountBody = discountBody;
    }

    public View render(@NonNull final ViewGroup parent) {
        final View mainContainer = ViewUtils.inflate(parent, R.layout.px_view_discount_detail);
        configureSummary(mainContainer);
        configureDescription(mainContainer);
        configureTermsAndConditions(mainContainer);
        return mainContainer;
    }

    private void configureSummary(@NonNull final View mainContainer) {
        final MPTextView summary = mainContainer.findViewById(R.id.summary);
        summary.setText(discountBody.getSummary());
    }

    private void configureDescription(@NonNull final View mainContainer) {
        final MPTextView description = mainContainer.findViewById(R.id.description);
        description.setText(discountBody.getDescription());
    }

    private void configureTermsAndConditions(@NonNull final View mainContainer) {
        final MPTextView linkText = mainContainer.findViewById(R.id.legal_terms);
        final TextUrl legalTerms = discountBody.getLegalTerms();
        linkText.setText(legalTerms.getContent());
        linkText.setOnClickListener(v -> TermsAndConditionsActivity
            .start(mainContainer.getContext(), legalTerms.getUrl()));
    }
}