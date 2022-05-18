package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.mercadopago.android.px.internal.util.TextUtil;

public final class GenericLocalized implements ILocalizedCharSequence {

    private final String text;
    private final int stringRes;

    public GenericLocalized(@Nullable final String text, @StringRes final int stringRes) {
        this.text = text;
        this.stringRes = stringRes;
    }

    @Nullable
    @Override
    public CharSequence get(@NonNull final Context context) {
        if (TextUtil.isNotEmpty(text)) {
            return text;
        } else if (stringRes != 0) {
            return context.getString(stringRes);
        }
        return null;
    }
}