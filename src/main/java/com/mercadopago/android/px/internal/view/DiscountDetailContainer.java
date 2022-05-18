package com.mercadopago.android.px.internal.view;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.viewmodel.DiscountDetailModel;
import com.mercadopago.android.px.internal.viewmodel.DiscountHeader;
import com.mercadopago.android.px.model.TextUrl;

public class DiscountDetailContainer {

    private final DiscountDetailModel discountDetailModel;

    /* default */ DiscountDetailContainer(@NonNull final DiscountDetailModel discountDetailModel) {
        this.discountDetailModel = discountDetailModel;
    }

    public void render(@NonNull final ViewGroup parent) {
        addDiscountTitle(parent);
        addDiscountDetail(parent);
    }

    private void addDiscountDetail(@NonNull final ViewGroup parent) {
        parent.addView(new DiscountDetail(discountDetailModel.getDiscountBody()).render(parent));
    }

    private void addDiscountTitle(@NonNull final ViewGroup parent) {
        final View headerView = ViewUtils.inflate(parent, R.layout.px_view_big_modal_title);
        final DiscountHeader discountHeader = discountDetailModel.getDiscountHeader();
        final TextUrl textUrl = discountHeader.getBadge();
        ((MPTextView) headerView.findViewById(R.id.title)).setText(discountHeader.getTitle());
        ViewUtils.loadOrGone(discountHeader.getSubtitle(), (headerView.findViewById(R.id.subtitle)));

        if (textUrl != null) {
            final Badge badge = headerView.findViewById(R.id.badge);
            badge.setText(textUrl.getContent());
            badge.setIconUrl(textUrl.getUrl());
            badge.setVisibility(View.VISIBLE);
        }

        parent.addView(headerView);
    }
}