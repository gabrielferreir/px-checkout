package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;

public class TitlePagerV2 extends TitlePager {

    public TitlePagerV2(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitlePagerV2(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void inflate() {
        inflate(getContext(), R.layout.px_view_title_pager, this);
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() != 3) {
            throw new RuntimeException("Incorrect number of children for Title Pager (must be 3)");
        }
        previousView = getChildAt(0);
        currentView = getChildAt(1);
        nextView = getChildAt(2);
        super.onFinishInflate();
    }
}
