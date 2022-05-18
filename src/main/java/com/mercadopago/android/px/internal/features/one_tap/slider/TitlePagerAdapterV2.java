package com.mercadopago.android.px.internal.features.one_tap.slider;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.view.TitlePager;

public class TitlePagerAdapterV2 extends TitlePagerAdapter {

    public TitlePagerAdapterV2(@NonNull final TitlePager titlePager) {
        super(titlePager);
    }

    @Override
    public void updatePosition(final float positionOffset, final int position) {
        // do nothing
    }
}
