package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IDetailDrawable {

    @Nullable
    Drawable getDrawable(@NonNull final Context context);
}
