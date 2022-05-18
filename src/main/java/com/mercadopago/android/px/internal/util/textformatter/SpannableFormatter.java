package com.mercadopago.android.px.internal.util.textformatter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import com.mercadopago.android.px.core.presentation.extensions.SpannableExtKt;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.internal.Text;

public class SpannableFormatter extends ChainFormatter {
    private static final String SEPARATOR = " ";
    private int textColor;
    private final Context context;
    private final SpannableStringBuilder spannableStringBuilder;
    private PxFont font;
    private boolean hasSpace;

    public SpannableFormatter(@NonNull final Context context) {
        this(new SpannableStringBuilder(), context);
    }

    public SpannableFormatter(@NonNull final SpannableStringBuilder spannableStringBuilder,
        @NonNull final Context context) {
        this.spannableStringBuilder = spannableStringBuilder;
        this.context = context;
        font = PxFont.REGULAR;
    }

    public SpannableFormatter withTextColor(final int color) {
        textColor = color;
        return this;
    }

    public SpannableFormatter withTextColor(@Nullable final String color) {
        if (TextUtil.isNotEmpty(color)) {
            //noinspection CatchMayIgnoreException
            try {
                textColor = Color.parseColor(color);
            } catch (final Exception e) {
            }
        }
        return this;
    }

    public SpannableFormatter withStyle(@NonNull final String name) {
        font = PxFont.from(name);
        return this;
    }

    public SpannableFormatter withStyle(@NonNull final PxFont pxFont) {
        font = pxFont;
        return this;
    }

    public SpannableFormatter withSpace() {
        hasSpace = true;
        return this;
    }

    public Spannable apply(@Nullable final Text text) {
        if (text == null) {
            return apply(TextUtil.EMPTY);
        }
        withStyle(text.getWeight());
        withTextColor(text.getTextColor());
        return apply(text.getMessage());
    }

    public Spannable apply(@StringRes final int resId) {
        final String text = resId != 0 ? context.getString(resId) : TextUtil.EMPTY;
        return apply(text);
    }

    @Override
    public Spannable apply(final CharSequence text) {
        final int indexStart = spannableStringBuilder.length();

        if (hasSpace) {
            spannableStringBuilder.append(SEPARATOR);
        }
        spannableStringBuilder.append(text);

        final int length = hasSpace ? SEPARATOR.length() + text.length() : text.length();

        SpannableExtKt.setColor(spannableStringBuilder, textColor, indexStart, indexStart + length);
        ViewUtils.setFontInSpannable(context, font, spannableStringBuilder, indexStart, indexStart + length);

        return spannableStringBuilder;
    }
}