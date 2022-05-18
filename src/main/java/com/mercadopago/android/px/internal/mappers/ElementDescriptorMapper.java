package com.mercadopago.android.px.internal.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.ElementDescriptorView;
import com.mercadopago.android.px.model.internal.SummaryInfo;

public class ElementDescriptorMapper extends Mapper<SummaryInfo, ElementDescriptorView.Model> {

    @Override
    public ElementDescriptorView.Model map(@NonNull final SummaryInfo summaryInfo) {
        return new ElementDescriptorView.Model(summaryInfo.getTitle(), summaryInfo.getSubtitle(),
            summaryInfo.getImageUrl(), R.drawable.px_review_item_default);
    }
}
