package com.mercadopago.android.px.internal.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import com.mercadolibre.android.picassodiskcache.PicassoDiskLoader;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText;
import com.mercadopago.android.px.internal.font.FontHelper;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.internal.Text;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public final class ViewUtils {

    private static final String TAG = "ViewUtils";
    private static final float DARKEN_FACTOR = 0.1f;
    private static final ColorMatrixColorFilter DISABLED_FILTER;

    static {
        final ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        final float[] lightingMatrix = {
            1, 0, 0, 0, 50,
            0, 1, 0, 0, 50,
            0, 0, 1, 0, 50,
            0, 0, 0, 0.9f, 0
        };

        matrix.postConcat(new ColorMatrix(lightingMatrix));
        DISABLED_FILTER = new ColorMatrixColorFilter(matrix);
    }

    private ViewUtils() {
    }

    public static boolean shouldVisibleAnim(@NonNull final View viewToAnimate) {
        return hasEndedAnim(viewToAnimate) && viewToAnimate.getVisibility() != VISIBLE;
    }

    public static boolean shouldGoneAnim(@NonNull final View viewToAnimate) {
        return hasEndedAnim(viewToAnimate) && viewToAnimate.getVisibility() != GONE;
    }

    public static boolean hasEndedAnim(@NonNull final View viewToAnimate) {
        return viewToAnimate.getAnimation() == null ||
            (viewToAnimate.getAnimation() != null && viewToAnimate.getAnimation().hasEnded());
    }

    public static boolean loadOrHide(final int visibility, @Nullable final Text text, @NonNull final MPTextView view) {
        if (text == null || TextUtil.isEmpty(text.getMessage())) {
            view.setVisibility(visibility);
            return false;
        } else {
            view.setText(text);
            view.setVisibility(VISIBLE);
            return true;
        }
    }

    public static boolean loadOrGone(@Nullable final PaymentCongratsText text, @NonNull final MPTextView view) {
        if (text == null || TextUtil.isEmpty(text.getMessage())) {
            view.setVisibility(View.GONE);
            return false;
        } else {
            view.setText(text);
            view.setVisibility(View.VISIBLE);
            return true;
        }
    }

    public static void loadOrGone(@Nullable final CharSequence text, @NonNull final TextView textView) {
        if (TextUtil.isEmpty(text)) {
            textView.setVisibility(GONE);
        } else {
            textView.setText(text);
            textView.setVisibility(VISIBLE);
        }
    }

    public static void loadLikeHtmlOrGone(@Nullable final CharSequence text, @NonNull final TextView textView) {
        if (TextUtil.isEmpty(text)) {
            textView.setVisibility(GONE);
        } else {
            textView.setText(HtmlCompat.fromHtml(text.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            textView.setVisibility(VISIBLE);
        }
    }

    public static void loadOrGone(@Nullable final Text text, @NonNull final MPTextView textView) {
        if (text == null || TextUtil.isEmpty(text.getMessage())) {
            textView.setVisibility(GONE);
        } else {
            textView.setText(text);
            textView.setVisibility(VISIBLE);
        }
    }

    public static void loadOrGone(@StringRes final int resId, @NonNull final TextView textView) {
        final CharSequence value = resId == 0 ? TextUtil.EMPTY : textView.getContext().getString(resId);
        loadOrGone(value, textView);
    }

    public static void loadOrGone(@DrawableRes final int resId, final ImageView imageView) {
        if (resId == 0) {
            imageView.setVisibility(GONE);
        } else {
            imageView.setImageResource(resId);
            imageView.setVisibility(VISIBLE);
        }
    }

    public static void loadOrGone(@Nullable final String imageUrl, final ImageView imageView) {
        if (TextUtil.isNotEmpty(imageUrl)) {
            PicassoDiskLoader.get(imageView.getContext()).load(imageUrl).into(imageView);
            imageView.setVisibility(VISIBLE);
        } else {
            imageView.setVisibility(GONE);
        }
    }

    public static void setMarginInView(@NonNull final View button, final int leftMargin, final int topMargin,
        final int rightMargin, final int bottomMargin) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        button.setLayoutParams(params);
    }

    public static void hideKeyboard(final Activity activity) {
        try {
            final EditText editText = (EditText) activity.getCurrentFocus();
            final InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (final Exception ex) { }
    }

    public static void openKeyboard(final View view) {
        view.requestFocus();
        final InputMethodManager imm =
            (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view.findFocus(), InputMethodManager.SHOW_IMPLICIT);
    }

    public static void setTextColor(@NonNull final TextView textView, @Nullable final String color) {
        if (TextUtil.isNotEmpty(color)) {
            try {
                textView.setTextColor(Color.parseColor(color));
            } catch (final Exception e) {
                logParseColorError(color);
            }
        }
    }

    public static void setBackgroundColor(@NonNull final View view, @Nullable final String color) {
        if (TextUtil.isNotEmpty(color)) {
            try {
                view.setBackgroundColor(Color.parseColor(color));
            } catch (final Exception e) {
                logParseColorError(color);
            }
        }
    }

    private static void logParseColorError(@Nullable final String color) {
        Logger.debug(TAG, "Cannot parse color" + color);
    }

    public static void setFontInSpannable(@NonNull final Context context, @NonNull final PxFont font,
        @NonNull final Spannable spannable) {
        setFontInSpannable(context, font, spannable, 0, spannable.length());
    }

    public static void setFontInSpannable(@NonNull final Context context, @NonNull final PxFont font,
        @NonNull final Spannable spannable, final int indexStart, final int indexEnd) {
        final Typeface typeface = FontHelper.getFont(context, font);
        spannable.setSpan(new StyleSpan(typeface != null ? typeface.getStyle() : font.fallbackStyle),
            indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static void stretchHeight(@NonNull final View view) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1.0f
        );
        view.setLayoutParams(params);
    }

    public static void wrapHeight(@NonNull final View view) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        view.setLayoutParams(params);
    }

    @NonNull
    public static View inflate(@NonNull final ViewGroup parent, @LayoutRes final int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    @NonNull
    public static LinearLayout createLinearContainer(final Context context) {
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    public static void cancelAnimation(@NonNull final View targetView) {
        final Animation animation = targetView.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
    }

    @ColorInt
    public static int getDarkPrimaryColor(@ColorInt final int primaryColor) {
        final float[] hsv = new float[3];
        Color.colorToHSV(primaryColor, hsv);
        hsv[1] = hsv[1] + DARKEN_FACTOR;
        hsv[2] = hsv[2] - DARKEN_FACTOR;
        return Color.HSVToColor(hsv);
    }

    /**
     * Paint the status bar
     *
     * @param color the color to use. The color will be darkened by {@link #DARKEN_FACTOR} percent
     */
    @SuppressLint({ "InlinedApi" })
    public static void setStatusBarColor(@ColorInt final int color, @NonNull final Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getDarkPrimaryColor(color));
    }

    public static boolean isScreenSize(@NonNull final Context context, final int screenLayoutSize) {
        return context.getResources().getConfiguration().isLayoutSizeAtLeast(screenLayoutSize);
    }
}
