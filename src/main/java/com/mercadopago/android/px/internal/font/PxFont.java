package com.mercadopago.android.px.internal.font;

import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadolibre.android.andesui.font.Font;

public enum PxFont {
    BLACK(0, Font.BLACK, Typeface.BOLD),
    BOLD(1, Font.BOLD, Typeface.BOLD),
    EXTRA_BOLD(2, Font.EXTRA_BOLD, Typeface.BOLD),
    LIGHT(3, Font.LIGHT, Typeface.NORMAL),
    REGULAR(4, Font.REGULAR, Typeface.NORMAL),
    SEMI_BOLD(5, Font.SEMI_BOLD, Typeface.BOLD),
    MEDIUM(6, Font.MEDIUM, Typeface.BOLD),
    THIN(7, Font.THIN, Typeface.NORMAL),
    MONOSPACE(8, null, Typeface.MONOSPACE.getStyle());

    public final int id;
    public final Font font;
    public final int fallbackStyle;

    PxFont(final int id, @Nullable final Font font, final int fallbackStyle) {
        this.id = id;
        this.font = font;
        this.fallbackStyle = fallbackStyle;
    }

    @NonNull
    public static PxFont from(final int id) {
        for (final PxFont font : values()) {
            if (font.id == id) {
                return font;
            }
        }
        return REGULAR;
    }

    @NonNull
    public static PxFont from(@Nullable final String name) {
        if (name == null) {
            return REGULAR;
        }
        for (final PxFont font : values()) {
            if (font.name().equalsIgnoreCase(name)) {
                return font;
            }
        }
        return REGULAR;
    }
}
