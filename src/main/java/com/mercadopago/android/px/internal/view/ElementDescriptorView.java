package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.AnimRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.extensions.ImageViewExtensionsKt;
import com.mercadopago.android.px.internal.util.TextUtil;

public class ElementDescriptorView extends LinearLayout {

    private static final int DEFAULT_MAX_LINES = -1;

    private TextView title;
    private TextView subtitle;
    private ImageView icon;
    private Animation appearAnimation;
    private Animation disappearAnimation;
    private Animation exitAnimation;
    private final Animation slideDownIn = AnimationUtils.loadAnimation(getContext(), R.anim.px_summary_slide_down_in);

    public ElementDescriptorView(final Context context) {
        this(context, null);
    }

    public ElementDescriptorView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElementDescriptorView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.PXElementDescriptorView,
            0, 0);
        float iconHeight;
        float iconWidth;
        float titleSize;
        int titleTextColor;
        int titleTextMaxLines;
        float subtitleSize;
        int subtitleTextColor;
        int subtitleTextMaxLines;
        int gravity;
        int appearAnimation;
        int disappearAnimation;
        int exitAnimation;
        try {
            iconHeight =
                a.getDimension(R.styleable.PXElementDescriptorView_px_element_icon_height, LayoutParams.WRAP_CONTENT);
            iconWidth =
                a.getDimension(R.styleable.PXElementDescriptorView_px_element_icon_width, LayoutParams.WRAP_CONTENT);
            titleSize = a.getDimensionPixelSize(R.styleable.PXElementDescriptorView_px_element_title_size,
                (int) context.getResources().getDimension(R.dimen.px_l_text));
            titleTextColor = a.getColor(R.styleable.PXElementDescriptorView_px_element_title_text_color, Color.BLACK);
            titleTextMaxLines =
                a.getInt(R.styleable.PXElementDescriptorView_px_element_title_max_lines, DEFAULT_MAX_LINES);
            subtitleSize = a.getDimensionPixelSize(R.styleable.PXElementDescriptorView_px_element_subtitle_size,
                (int) context.getResources().getDimension(R.dimen.px_l_text));
            subtitleTextColor =
                a.getColor(R.styleable.PXElementDescriptorView_px_element_subtitle_text_color, Color.BLACK);
            subtitleTextMaxLines =
                a.getInt(R.styleable.PXElementDescriptorView_px_element_subtitle_max_lines, DEFAULT_MAX_LINES);
            gravity = a.getInteger(R.styleable.PXElementDescriptorView_android_gravity, Gravity.CENTER);
            appearAnimation = a.getResourceId(R.styleable.PXElementDescriptorView_px_appear_animation, -1);
            disappearAnimation = a.getResourceId(R.styleable.PXElementDescriptorView_px_disappear_animation, -1);
            exitAnimation = a.getResourceId(R.styleable.PXElementDescriptorView_px_exit_animation, -1);
        } finally {
            a.recycle();
        }

        init(iconWidth, iconHeight, titleSize, titleTextColor, titleTextMaxLines, subtitleSize, subtitleTextColor,
            subtitleTextMaxLines, gravity, appearAnimation, disappearAnimation, exitAnimation);
    }

    public void setIconSize(final int width, final int height) {
        final LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) icon.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        icon.setLayoutParams(layoutParams);
    }

    private void init(final float iconWidth, final float iconHeight, final float titleSize, final int titleTextColor,
        final int titleTextMaxLines, final float subtitleSize, final int subtitleTextColor,
        final int subtitleTextMaxLines, final int gravity,
        @AnimRes final int appearAnimation, @AnimRes final int disappearAnimation, @AnimRes final int exitAnimation) {
        inflate(getContext(), R.layout.px_view_element_descriptor, this);
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        icon = findViewById(R.id.icon);
        setIconSize((int) iconWidth, (int) iconHeight);
        configureTextView(title, titleSize, titleTextColor, titleTextMaxLines, gravity);
        configureTextView(subtitle, subtitleSize, subtitleTextColor, subtitleTextMaxLines, gravity);
        setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        post(() -> title.performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null));
        this.appearAnimation = AnimationUtils.loadAnimation(getContext(), appearAnimation);
        this.disappearAnimation = AnimationUtils.loadAnimation(getContext(), disappearAnimation);
        this.exitAnimation = AnimationUtils.loadAnimation(getContext(), exitAnimation);
    }

    private void configureTextView(final TextView text, final float textSize, final int textColor,
        final int textMaxLines,
        final int gravity) {
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        text.setTextColor(textColor);
        if (textMaxLines != DEFAULT_MAX_LINES) {
            text.setMaxLines(textMaxLines);
        }
        final LayoutParams titleParams = (LayoutParams) text.getLayoutParams();
        titleParams.gravity = gravity;
        text.setLayoutParams(titleParams);
    }

    public void update(@NonNull final ElementDescriptorView.Model model) {
        title.setText(model.getTitle());

        if (model.hasSubtitle()) {
            subtitle.setVisibility(VISIBLE);
            subtitle.setText(model.getSubtitle());
        } else {
            subtitle.setVisibility(GONE);
        }

        ImageViewExtensionsKt.loadOrElse(icon, model.getUrlIcon(), model.getIconResourceId(), new CircleTransform());
    }

    public void animateExit(@Nullable final Long duration) {
        if (duration != null) {
            exitAnimation.setDuration(duration);
        }
        startAnimation(exitAnimation);
    }

    public void animateAppear(@NonNull final Boolean shouldSlide) {
        startAnimation(shouldSlide ? slideDownIn : appearAnimation);
    }

    public void animateDisappear() {
        startAnimation(disappearAnimation);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
    }

    public static class Model {

        @NonNull private final String title;
        @Nullable private final String subtitle;
        @NonNull private final String urlIcon;
        @DrawableRes private final int resourceIdIcon;

        public Model(@NonNull final String title, @Nullable final String subtitle, @Nullable final String urlIcon,
            @DrawableRes final int defaultResource) {
            this.title = title;
            this.subtitle = subtitle;
            this.urlIcon = urlIcon == null ? "" : urlIcon;
            resourceIdIcon = defaultResource;
        }

        @NonNull
            /* default */ String getTitle() {
            return title;
        }

        @Nullable
            /* default */ String getSubtitle() {
            return subtitle;
        }

        @NonNull
            /* default */ String getUrlIcon() {
            return urlIcon;
        }

        @DrawableRes
            /* default */ int getIconResourceId() {
            return resourceIdIcon;
        }

        /* default */ boolean hasSubtitle() {
            return TextUtil.isNotEmpty(subtitle);
        }
    }
}
