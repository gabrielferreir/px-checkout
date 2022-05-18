package com.mercadopago.android.px.internal.features.payment_result.components;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.CompactComponent;

public class LineSeparator extends CompactComponent<LineSeparator.Props, Void> {

    public LineSeparator(final Props props) {
        super(props);
    }

    public static class Props {
        /* default */ final int color;

        public Props(@ColorRes final int color) {
            this.color = color;
        }
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final View view = ViewUtils.inflate(parent, R.layout.px_view_separator);
        view.setBackgroundColor(parent.getContext().getResources().getColor(props.color));
        return view;
    }
}
